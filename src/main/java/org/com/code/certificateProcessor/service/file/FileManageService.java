package org.com.code.certificateProcessor.service.file;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import de.huxhorn.sulky.ulid.ULID;
import org.com.code.certificateProcessor.exeption.OSSException;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.pojo.dto.oss.PartETagDTO;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileManageService {
    @Autowired
    private OSSService ossService;
    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    AwardSubmissionMapper awardSubmissionMapper;

    /**
     * 每段的大小5MB
     * 最大分片数3
     * 多次上传片段的总和最大 5 * 3 = 15MB
     */
    public static final long PART_SIZE = 5 * 1024 * 1024;
    public static final long MAX_PART_COUNT = 2;
    public static final long MAX_TOTAL_SIZE = PART_SIZE * MAX_PART_COUNT;

    public static final String UPLOAD_ID_KEY_PREFIX = "OSSUploadId: ";
    /**
     * 记录文件是否临时被撤销的 key
     * 当 IfSubmissionGotRevoked uploadId 0   代表文件正常
     * 当 IfSubmissionGotRevoked uploadId 1   代表文件被撤销
     */
    public static final String IfSubmissionGotRevoked = "IfSubmissionGotRevoked";

    public static final Set<String> VALID_FILE_MIME_TYPES = new HashSet(Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"));
    public boolean isValidFile(String detectedMimeType) {
        if (VALID_FILE_MIME_TYPES.contains(detectedMimeType)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param fileName
     * @param fileSize
     * @return
     */
    public Map<String, Object> initMultipartUpload(String fileName,String detectedMimeType,long fileSize){
        try {
            //生成文件在OSS存储的路径
            String imageObjectKey = ossService.getObjectKey(fileName,detectedMimeType);
            //给文件分成许多小段
            int totalPartCount = (int) Math.ceil((double) fileSize / PART_SIZE);

            String uploadId = ossService.getUploadId(detectedMimeType, imageObjectKey);

            Map<String, Object> uploadInfo = new HashMap<>();
            uploadInfo.put("studentId", SecurityContextHolder.getContext().getAuthentication().getName());
            uploadInfo.put("fileName", fileName);
            uploadInfo.put("imageObjectKey", imageObjectKey);
            uploadInfo.put("fileSize", fileSize);
            uploadInfo.put("totalPartCount", totalPartCount);
            uploadInfo.put("completedPartNumber", 0);
            uploadInfo.put("currentUploadedFileSize",0L);
            uploadInfo.put("completedPartETags", new ArrayList<PartETagDTO>());

            objectRedisTemplate.opsForValue().set(UPLOAD_ID_KEY_PREFIX+uploadId, uploadInfo, Duration.ofHours(1));

            Map<String, Object> map = new HashMap<>();
            map.put("uploadId", uploadId);
            map.put("totalPartCount", totalPartCount);
            map.put("completedPartNumber", 0);
            return map;
        }catch (Exception e) {
            throw new OSSException("初始化上传失败，请重新上传");
        }
    }



    public void uploadPart(String uploadId, Map<String, Object> uploadInfo, int chunkSerialNumber, InputStream inputStream,long fileChunkSize) throws Exception {
        try {
            String imageObjectKey = (String) uploadInfo.get("imageObjectKey");

            UploadPartResult uploadPartResult = ossService.getUploadPartResult(uploadId, chunkSerialNumber, inputStream, fileChunkSize, imageObjectKey);

            int completedPartNumber = (int)uploadInfo.get("completedPartNumber");
            long currentUploadedFileSize = (long) uploadInfo.get("currentUploadedFileSize");
            uploadInfo.put("completedPartNumber", completedPartNumber + 1);
            uploadInfo.put("currentUploadedFileSize", currentUploadedFileSize + fileChunkSize);

            List<PartETagDTO> completedPartETags = (List<PartETagDTO>) uploadInfo.get("completedPartETags");
            completedPartETags.add(new PartETagDTO(chunkSerialNumber, uploadPartResult.getPartETag().getETag()));
            uploadInfo.put("completedPartETags", completedPartETags);

            objectRedisTemplate.opsForValue().set(UPLOAD_ID_KEY_PREFIX + uploadId, uploadInfo);
        } catch (Exception e) {
            throw new OSSException("上传失败，请重新上传");
        }
    }



    public Map<String, Object> completeMultipartUpload(String uploadId,Map<String, Object> uploadInfo) {
        try {
            List<PartETagDTO> completedPartETags = (List<PartETagDTO>) uploadInfo.get("completedPartETags");
            if (completedPartETags == null || completedPartETags.isEmpty()) {
                throw new OSSException("上传失败，请重新上传");
            }
            completedPartETags.sort(Comparator.comparingInt(PartETagDTO::getPartNumber));
            List<PartETag> partETags = completedPartETags.stream().map(PartETagDTO::toPartETag).collect(Collectors.toList());

            String imageObjectKey = (String) uploadInfo.get("imageObjectKey");
            ossService.completeMultipartUploadRequest(uploadId, imageObjectKey, partETags);

            Map<String, Object> map = new HashMap<>();
            String imageTemporaryUrl = ossService.generateTemporaryUrl(imageObjectKey,180);

            map.put("imageTemporaryUrl", imageTemporaryUrl);
            map.put("submissionId", uploadInfo.get("submissionId"));
            map.put("status", AwardSubmissionStatus.AI_PROCESSING);
            map.put("studentId",SecurityContextHolder.getContext().getAuthentication().getName());

            ULID ulid = new ULID();
            String submissionId = ulid.nextULID();
            awardSubmissionMapper.addAwardSubmission(Map.of(
                    "submissionId",submissionId,
                    "studentId",uploadInfo.get("studentId"),
                    "imageObjectKey", imageObjectKey));

            objectRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations ops) throws DataAccessException {
                    // 开启事务
                    ops.multi();

                    ops.delete(UPLOAD_ID_KEY_PREFIX + uploadId);
                    ops.opsForHash().put(IfSubmissionGotRevoked,submissionId,"0");

                    // 提交事务并返回结果
                    return ops.exec();
                }
            });

            return map;
        } catch (Exception e) {
            throw new OSSException("结束分段失败",e);
        }
    }
}
