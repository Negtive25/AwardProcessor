package org.com.code.certificateProcessor.rocketMQ.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.AwardClassification;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.DeduplicationResult;
import org.com.code.certificateProcessor.LangChain4j.service.ClassificationService;
import org.com.code.certificateProcessor.LangChain4j.service.OCRService;
import org.com.code.certificateProcessor.exeption.RocketmqException;
import org.com.code.certificateProcessor.mapper.StandardAwardMapper;
import org.com.code.certificateProcessor.mapper.StudentMapper;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.enums.ContentType;
import com.alibaba.fastjson.JSONObject;
import dev.langchain4j.exception.LangChain4jException;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.com.code.certificateProcessor.ElasticSearch.Service.ElasticUtil;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.AwardInfo;
import dev.langchain4j.data.image.Image;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.rocketMQ.MQConstants;
import org.com.code.certificateProcessor.service.file.FileManageService;
import org.com.code.certificateProcessor.util.OssImageCompressor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets; // 3. 导入 Charsets
import java.util.List;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = MQConstants.Topic.SUBMISSION,
        consumerGroup = MQConstants.Consumer.STUDENT_AWARD_SUBMISSION_CONSUMER,
        selectorExpression = MQConstants.Tag.STUDENT_AWARD_SUBMISSION,
        messageModel = MessageModel.CLUSTERING,
        maxReconsumeTimes = 3
)
public class studentAwardSubmissionConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private OCRService ocrService;
    @Autowired
    private AwardSubmissionMapper awardSubmissionMapper;
    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    private ElasticUtil elasticUtil;
    @Autowired
    private StandardAwardMapper standardAwardMapper;
    @Autowired
    private ClassificationService classificationService;
    @Autowired
    StudentMapper studentMapper;

    private static final int CANDIDATE_AWARD_NUM = 5;

    @Override
    public void onMessage(MessageExt message) {
        String completeUploadInfoJsonMessage = new String(message.getBody(), StandardCharsets.UTF_8);
        /**
         * completeUploadInfo 包含:
         * String imageUrl ,String submissionId ,AwardSubmissionStatus status
         */
        Map<String, Object> completeUploadInfo = JSONObject.parseObject(completeUploadInfoJsonMessage, Map.class);
        String submissionId = completeUploadInfo.get("submissionId").toString();
        try {
            /**
             * 记录文件是否临时被撤销的 key
             * 当 IfSubmissionGotRevoked uploadId 0   代表文件正常
             * 当 IfSubmissionGotRevoked uploadId 1   代表文件被撤销
             */
            String ifSubmissionGotRevoked = (String) objectRedisTemplate.opsForHash().get(FileManageService.IfSubmissionGotRevoked,submissionId);
            if(ifSubmissionGotRevoked != null && ifSubmissionGotRevoked.equals("1")){
                objectRedisTemplate.opsForHash().delete(FileManageService.IfSubmissionGotRevoked,submissionId);
                return;
            }

            String imageUrl = completeUploadInfo.get("imageUrl").toString();
            String compressedImageURL = OssImageCompressor.getAdaptiveCompressedUrl(imageUrl);

            AwardInfo awardInfo;
            try {
                Image image = Image.builder()
                        .url(compressedImageURL)
                        .build();
                awardInfo = ocrService.getOcrAgent().extractWordFromPicture(image);
            } catch (Exception e) {
                throw new LangChain4jException("视觉模型分析图片发生错误", e);
            }

            String studentName = studentMapper.getStudentNameById(completeUploadInfo.get("studentId").toString());

            if(awardInfo.getIfCertification().equals("No")){
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId",completeUploadInfo.get("submissionId"),
                        "status", AwardSubmissionStatus.AI_REJECTED,
                        "ocrFullText",awardInfo.toMap(),
                        "rejectionReason", "不是一个奖项"));
            }else if(awardInfo.getStudentName() == null||!awardInfo.getStudentName().equals(studentName)){
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId",completeUploadInfo.get("submissionId"),
                        "status", AwardSubmissionStatus.AI_REJECTED,
                        "ocrFullText",awardInfo.toMap(),
                        "rejectionReason","图片中没有出现学生姓名或者学生姓名不匹配"));
            }
            else{
                /**
                 * 如果是奖状，则 awardInfo 包含以下字段信息
                 *     {
                 *       "studentName": "学生姓名",
                 *       "awardName": "标准奖项名称"
                 *       "awardDate": "2025年12月9日"
                 *       "ifCertification":"Yes"
                 *     }
                 */
                List<String> rankedAwardIds;
                try {
                    rankedAwardIds = elasticUtil.hybridSearch(awardInfo.getAwardName(), ContentType.STANDARD_AWARD, List.of("awardName"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> candidateAwardIds = rankedAwardIds.stream().limit(CANDIDATE_AWARD_NUM).toList();
                List<StandardAward> standardAwards = standardAwardMapper.getAwardsByBatchId(candidateAwardIds);

                AwardClassification awardClassification =classificationService.getClassificationAgent().classifyAward(awardInfo,standardAwards);

                if(!awardClassification.getMatchFound()){
                    awardSubmissionMapper.updateAwardSubmission(Map.of(
                            "submissionId",completeUploadInfo.get("submissionId"),
                            "status", AwardSubmissionStatus.AI_REJECTED,
                            "ocrFullText",awardInfo.toMap(),
                            "rejectionReason","没有匹配的奖项"));
                }

                List<AwardSubmission> awardSubmissions = awardSubmissionMapper.getApprovedSubmissionByStudentId(
                        completeUploadInfo.get("studentId").toString()
                );

                DeduplicationResult deduplicationResult = classificationService.getClassificationAgent().checkForDuplicate(awardInfo,awardSubmissions);
                if(deduplicationResult.getDuplicated()){
                    awardSubmissionMapper.updateAwardSubmission(Map.of(
                            "submissionId",completeUploadInfo.get("submissionId"),
                            "status", AwardSubmissionStatus.AI_REJECTED,
                            "ocrFullText",awardInfo.toMap(),
                            "duplicateCheckResult",deduplicationResult.getDuplicated(),
                            "rejectionReason",deduplicationResult.getReasoning()));
                } else{
                    List<Map<String, Object>> standardAwardList = standardAwards.stream().map(StandardAward::toMap).toList();
                    awardSubmissionMapper.updateAwardSubmission(Map.of(
                            "submissionId",completeUploadInfo.get("submissionId"),
                            "status", AwardSubmissionStatus.AI_APPROVED,
                            "ocrFullText",awardInfo.toMap(),
                            "matchedAwardId",awardClassification.getMatchedAwardId(),
                            "aiSuggestion","可能匹配的奖项:\n"+standardAwardList));
                }
            }
        } catch (Exception e) {
            // 核心容错逻辑
            int retryCount = message.getReconsumeTimes();

            // 我们要重试3次 (retryCount 0, 1, 2)
            if (retryCount >= 2) {
                // 这是第3次失败 (retryCount=2)，不再重试
                LoggerFactory.getLogger(studentAwardSubmissionConsumer.class)
                        .error("消息处理失败 3 次, submissionId: {}. 放弃重试，转为人工审核。", e);

                // 更新数据库状态为 AI_ERROR_MANUAL_REVIEW
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId", submissionId,
                        "status", AwardSubmissionStatus.AI_ERROR_MANUAL_REVIEW,
                        "rejectionReason", "AI处理异常: " + e.getMessage() // 记录异常信息
                ));

                // 关键：不抛出异常，消息被“成功消费”，不会再重试或进入死信队列

            } else {
                // 第1次(retryCount=0)或第2次(retryCount=1)失败，抛出异常以触发重试
                LoggerFactory.getLogger(studentAwardSubmissionConsumer.class)
                        .warn("消息处理失败, submissionId: {}. 尝试次数: {}/3. 即将重试...", retryCount + 1, e);

                // 抛出运行时异常，RocketMQ将自动重试此消息
                throw new RocketmqException("AI处理失败，触发重试", e);
            }
        }
    }
}
