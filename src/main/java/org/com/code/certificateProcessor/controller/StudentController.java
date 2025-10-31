package org.com.code.certificateProcessor.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.apache.tika.Tika;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.CreateGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.SignInGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.UpdateGroup;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.com.code.certificateProcessor.rocketMQ.producer.SubmissionProducer;
import org.com.code.certificateProcessor.service.awardSubmission.AwardSubmissionService;
import org.com.code.certificateProcessor.service.student.StudentService;
import org.com.code.certificateProcessor.service.file.FileManageService;
import org.com.code.certificateProcessor.validation.ValidEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.com.code.certificateProcessor.rocketMQ.MQConstants;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@Validated
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private FileManageService fileManageService;
    @Autowired
    private AwardSubmissionService awardSubmissionService;
    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    private SubmissionProducer submissionProducer;

    @PostMapping("/signUp")
    public ResponseHandler signUp(
            @Validated(CreateGroup.class) @RequestBody StudentRequest studentRequest) {

        Student student = new Student();
        student.setStudentId(studentRequest.getStudentId());
        student.setPassword(studentRequest.getPassword());
        student.setName(studentRequest.getName());
        student.setCollege(studentRequest.getCollege());
        student.setMajor(studentRequest.getMajor());
        student.setAuth(Auth.STUDENT.getType());

        studentService.addStudent(student);
        return new ResponseHandler(ResponseHandler.SUCCESS, "注册成功");
    }
    @PostMapping("/signIn")
    public ResponseHandler signIn(@Validated(SignInGroup.class) @RequestBody StudentRequest studentRequest) {
        String token = studentService.studentSignIn(studentRequest.getStudentId(),
                studentRequest.getPassword());
        return new ResponseHandler(ResponseHandler.SUCCESS, "登录成功,获取token", token);
    }

    @GetMapping("/me")
    public ResponseHandler me() {
        String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentById(studentId);
        List<Double> scoreList = awardSubmissionService.sumApprovedScoreByStudentId(List.of(studentId));
        student.setSumOfScore(scoreList.get(0));
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取当前用户信息成功", student);
    }

    /**
     *
     * @param fileName 比如 doc.txt
     * @param fileSize 单位 字节
     * @return
     */
    @PostMapping("/initSubmission")
    public ResponseHandler initSubmission(@NotBlank @RequestParam("fileName") String fileName,
                                           @RequestParam("fileSize") long fileSize) {
        String detectedMimeType = new Tika().detect(fileName);

        if (!fileManageService.isValidFile(detectedMimeType))
            return new ResponseHandler(ResponseHandler.BAD_REQUEST, "文件类型错误");

        Map<String, Object> uploadInfo = fileManageService.initMultipartUpload(fileName,detectedMimeType, fileSize);
        return new ResponseHandler(ResponseHandler.SUCCESS, "初始化上传成功", uploadInfo);
    }

    /**
     *
     * @param fileChunk
     * @param chunkSerialNumber 从1开始
     * @param uploadId
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadPart")
    public ResponseHandler awardSubmission(@RequestParam("fileChunk") MultipartFile fileChunk,
                                           @RequestParam("chunkSerialNumber") int chunkSerialNumber,
                                           @RequestParam("uploadId")@NotBlank(message = "上传ID不能为空") String uploadId) throws Exception {
        Map<String, Object> uploadInfo = (Map<String, Object>) objectRedisTemplate.opsForValue().get(fileManageService.UPLOAD_ID_KEY_PREFIX + uploadId);
        if (uploadInfo == null)
            return new ResponseHandler(ResponseHandler.NOT_FOUND, "上传信息不存在");

        int totalPartCount = (int) uploadInfo.get("totalPartCount");
        if (chunkSerialNumber > totalPartCount|| chunkSerialNumber < 1)
            return new ResponseHandler(ResponseHandler.BAD_REQUEST, "分片序号超出范围");

        long fileChunkSize = fileChunk.getSize();

        // 只在处理第一个分片时检查文件类型
        if (chunkSerialNumber == 1) { // 假设分片从1开始
            Tika tika = new Tika();
            try (InputStream inputStream = fileChunk.getInputStream()) {
                if (fileChunkSize > fileManageService.PART_SIZE)
                    return new ResponseHandler(ResponseHandler.BAD_REQUEST, "分片大小超过限制");

                /**
                 * 先检查 fileChunkSize 的大小再检查文件类型，
                 * 因为检查 文件类型 要读取整个流，耗时耗性能，
                 * 如果是检查文件类型成功结果发现文件太大了报错，那就白白花那么多时间和性能检查类型了
                 * 还不如再一开始的时候先检查文件大小再检测文件类型更合理
                 */
                String detectedMimeType = tika.detect(inputStream); // Tika会读取流来检测
                if (!fileManageService.isValidFile(detectedMimeType))
                    return new ResponseHandler(ResponseHandler.BAD_REQUEST, "文件类型错误");
            }
        }


        try (InputStream inputStream = fileChunk.getInputStream()){
            if (fileChunkSize > fileManageService.PART_SIZE)
                return new ResponseHandler(ResponseHandler.BAD_REQUEST, "分片大小超过限制");

            if(fileChunkSize+ (long) uploadInfo.get("currentUploadedFileSize")>fileManageService.MAX_TOTAL_SIZE){
                return new ResponseHandler(ResponseHandler.BAD_REQUEST, "总的文件大小超出限制");
            }
            fileManageService.uploadPart(uploadId,uploadInfo, chunkSerialNumber, inputStream,fileChunkSize);
        }
        return new ResponseHandler(ResponseHandler.SUCCESS, "分片提交成功");
    }

    @PostMapping("/completeSubmission")
    public ResponseHandler completeSubmission(@RequestParam("uploadId") String uploadId) {
        Map<String, Object> uploadInfo = (Map<String, Object>) objectRedisTemplate.opsForValue().get(fileManageService.UPLOAD_ID_KEY_PREFIX + uploadId);
        if (uploadInfo == null)
            return new ResponseHandler(ResponseHandler.NOT_FOUND, "上传信息不存在");

        Map<String, Object> completeUploadInfo = fileManageService.completeMultipartUpload(uploadId, uploadInfo);
        /**
         * completeUploadInfo 包含:
         * imageUrl ,submissionId ,status
         */
        submissionProducer.asyncSendMessage(completeUploadInfo, MQConstants.Topic.SUBMISSION, MQConstants.Tag.STUDENT_AWARD_SUBMISSION);
        return new ResponseHandler(ResponseHandler.SUCCESS, "上传完成", completeUploadInfo);
    }

    @DeleteMapping("/revokeSubmission")
    public ResponseHandler revokeSubmission(@RequestParam("submissionId") String submissionId) {
        awardSubmissionService.revokeSubmission(submissionId,SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseHandler(ResponseHandler.SUCCESS, "撤销提交成功");
    }

    @GetMapping("/getSubmissionProgress")
    public ResponseHandler getSubmissionProgress(@Valid @RequestBody CursorPageRequest cursorPageRequest,
                                                 @RequestParam List<@ValidEnum(enumClass = AwardSubmissionStatus.class) String> status) {
        String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
        CursorPageResponse<AwardSubmission> submissionProgress = awardSubmissionService.cursorQuerySubmissionByStatus(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize(),studentId, status);

        return new ResponseHandler(ResponseHandler.SUCCESS, "获取提交进度成功", submissionProgress);
    }

    @PutMapping("/updateInfo")
    public ResponseHandler updateInfo(@Validated(UpdateGroup.class)@RequestBody StudentRequest studentRequest) {
        studentService.updateStudentInfo(studentRequest);
        return new ResponseHandler(ResponseHandler.SUCCESS, "更新学生信息成功");
    }
}
