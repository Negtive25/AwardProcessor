package org.com.code.certificateProcessor.service.file;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.com.code.certificateProcessor.exeption.OSSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OSSService {

    private final String BUCKET_NAME;

    OSSClient ossClient;

    public OSSService(@Value("${aliyun.oss.endpoint}")String ENDPOINT,
                      @Value("${aliyun.oss.accessKeyId}")String ACCESS_KEY_ID,
                      @Value("${aliyun.oss.accessKeySecret}")String ACCESS_KEY_SECRET,
                      @Value("${aliyun.oss.bucketName}")String BUCKET_NAME) {
        this.BUCKET_NAME = BUCKET_NAME;

        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        if(!ossClient.doesBucketExist(BUCKET_NAME)){
            throw new OSSException("存储桶不存在");
        }
        this.ossClient=ossClient;
    }

    // 获取OSS文件路径
    public String getObjectKey(String fileName, String mimeType){
        StringBuilder objectKey = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = now.format(formatter);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return  objectKey.append(mimeType).append("/").append(formattedDate).append("/")
                .append(SecurityContextHolder.getContext().getAuthentication().getName()).append("/")
                .append(uuid).append("-").append(fileName).toString();
    }

    public String getUploadId(String detectedMimeType, String imageObjectKey) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(
                BUCKET_NAME, imageObjectKey);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(detectedMimeType);
        request.setObjectMetadata(objectMetadata);

        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        return uploadId;
    }

    public UploadPartResult getUploadPartResult(String uploadId, int chunkSerialNumber, InputStream inputStream, long fileChunkSize, String imageObjectKey) {
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(BUCKET_NAME);
        uploadPartRequest.setKey(imageObjectKey);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setPartNumber(chunkSerialNumber);
        uploadPartRequest.setInputStream(inputStream);
        uploadPartRequest.setPartSize(fileChunkSize);

        // 执行上传操作
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        return uploadPartResult;
    }

    public void completeMultipartUploadRequest(String uploadId, String imageObjectKey, List<PartETag> partETags) {
        CompleteMultipartUploadRequest completeMultipartUploadRequest
                = new CompleteMultipartUploadRequest(BUCKET_NAME, imageObjectKey, uploadId, partETags);
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public void deleteFile(String objectKey){
        try {
            /**
             *  objectKey = "video/20250525/19233392121872384/94c0d50c88524b0494f76ae8112b1bf1-肯德基疯狂星期四.mp4"
             */
            ossClient.deleteObject(BUCKET_NAME, objectKey);
        } catch (Exception e) {
            throw new OSSException("删除文件失败",e);
        }
        ossClient.shutdown();
    }

    /**
     * ACL 设为 private + 使用临时签名 URL（Presigned URL）
     * 防止
     * 1.URL 被猜到 ,ACL=private，没签名打不开
     * 2.URL 被泄露（比如复制给别人）,签名 URL 有短时效（如5分钟），过期失效
     * 3.盗链（其他网站 <img src="你的图">） 签名 URL 无法被第三方长期使用；还可配合 Referer 防盗链
     * 4.未授权用户访问 后端可以在生成 URL 前做权限校验
     * @param objectKey
     * @param expireSeconds
     * @return
     */
    public String generateTemporaryUrl(String objectKey, int expireSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, objectKey, HttpMethod.GET);
        request.setExpiration(expiration);

        return ossClient.generatePresignedUrl(request).toString();
    }
}
