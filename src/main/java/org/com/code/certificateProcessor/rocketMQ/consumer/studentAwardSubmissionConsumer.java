package org.com.code.certificateProcessor.rocketMQ.consumer;

import org.com.code.certificateProcessor.LangChain4j.agent.AwardClassification;
import org.com.code.certificateProcessor.LangChain4j.agent.ClassificationAgent;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.DeduplicationResult;
import org.com.code.certificateProcessor.mapper.StandardAwardMapper;
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
import org.com.code.certificateProcessor.LangChain4j.service.EmbeddingService;
import org.com.code.certificateProcessor.LangChain4j.service.MultiModelService;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.rocketMQ.MQConstants;
import org.com.code.certificateProcessor.service.file.FileManageService;
import org.com.code.certificateProcessor.util.OssImageCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = MQConstants.Topic.SUBMISSION,
        consumerGroup = MQConstants.Consumer.STUDENT_AWARD_SUBMISSION_CONSUMER,
        selectorExpression = MQConstants.Tag.STUDENT_AWARD_SUBMISSION,
        messageModel = MessageModel.CLUSTERING
)
public class studentAwardSubmissionConsumer implements RocketMQListener<String> {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private MultiModelService multiModelService;
    @Autowired
    private AwardSubmissionMapper awardSubmissionMapper;
    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    private ElasticUtil elasticUtil;
    @Autowired
    private StandardAwardMapper standardAwardMapper;
    @Autowired
    @Qualifier("ClassificationAgent")
    private ClassificationAgent classificationAgent;

    private static final Logger logger = LoggerFactory.getLogger(studentAwardSubmissionConsumer.class);
    private static final int CANDIDATE_AWARD_NUM = 5;

    @Override
    public void onMessage(String completeUploadInfoJsonMessage) {
        /**
         * completeUploadInfo 包含:
         * String imageUrl ,String submissionId ,AwardSubmissionStatus status
         */
        Map<String, Object> completeUploadInfo = JSONObject.parseObject(completeUploadInfoJsonMessage, Map.class);
        String submissionId = completeUploadInfo.get("submissionId").toString();
        logger.info("开始处理奖状提交，submissionId: {}", submissionId);
        /**
         * 记录文件是否临时被撤销的 key
         * 当 IfSubmissionGotRevoked uploadId 0   代表文件正常
         * 当 IfSubmissionGotRevoked uploadId 1   代表文件被撤销
         */
        String ifSubmissionGotRevoked = (String) objectRedisTemplate.opsForHash().get(FileManageService.IfSubmissionGotRevoked,submissionId);
        if("1".equals(ifSubmissionGotRevoked)){
            objectRedisTemplate.opsForHash().delete(FileManageService.IfSubmissionGotRevoked,submissionId);
            logger.warn("检测到奖状提交已被撤销，停止处理，submissionId: {}", submissionId);
            return;
        }

        String imageUrl = completeUploadInfo.get("imageUrl").toString();
        String compressedImageURL = OssImageCompressor.getAdaptiveCompressedUrl(imageUrl);

        AwardInfo awardInfo;
        try {
            logger.info("调用视觉模型分析图片，submissionId: {}", submissionId);
            awardInfo = multiModelService.extractWordFromPicture(compressedImageURL);
            logger.info("视觉模型分析成功，submissionId: {}", submissionId);
        } catch (Exception e) {
            logger.error("视觉模型分析图片时发生错误，submissionId: {}", submissionId, e);
            throw new LangChain4jException("视觉模型分析图片发生错误", e);
        }


        if(awardInfo.getIfCertification().equals("No")){
            logger.warn("AI模型判断图片不是奖状，拒绝处理，submissionId: {}", submissionId);
            awardSubmissionMapper.updateAwardSubmission(Map.of(
                    "submissionId",completeUploadInfo.get("submissionId"),
                    "status", AwardSubmissionStatus.AI_REJECTED,
                    "ocrFullText",awardInfo.toMap(),
                    "rejectionReason", "不是一个奖项"));
        }else if(awardInfo.getStudentName() == null||!awardInfo.getStudentName().equals(completeUploadInfo.get("studentName"))){
            logger.warn("图片中的学生姓名与提交者不匹配，拒绝处理，submissionId: {}", submissionId);
            awardSubmissionMapper.updateAwardSubmission(Map.of(
                    "submissionId",completeUploadInfo.get("submissionId"),
                    "status", AwardSubmissionStatus.AI_REJECTED,
                    "ocrFullText",awardInfo.toMap(),
                    "rejectionReason","图片中没有出现学生姓名或者学生姓名不匹配"));
        }
        else{
            logger.info("图片初步校验通过，开始进行奖项匹配，submissionId: {}", submissionId);
            /**
             * 如果是奖状，则 awardInfo 包含以下字段信息
             *     {
             *       "studentName": "学生姓名",
             *       "awardName": "标准奖项名称"
             *       "awardDate": "2025年12月9日"
             *       "ifCertification":"Yes"
             *     }
             */

            float[] embedding = embeddingService.getEmbedding(awardInfo.getAwardName());
            List<String> rankedAwardIds;
            try {
                logger.info("开始从Elasticsearch中检索候选奖项，submissionId: {}", submissionId);
                rankedAwardIds = elasticUtil.hybridSearch(awardInfo.getAwardName(), ContentType.STANDARD_AWARD, List.of("awardName"));
                logger.info("Elasticsearch检索成功，候选奖项数量: {}，submissionId: {}", rankedAwardIds.size(), submissionId);
            } catch (IOException e) {
                logger.error("从Elasticsearch中检索候选奖项时发生错误，submissionId: {}", submissionId, e);
                throw new RuntimeException(e);
            }
            List<String> candidateAwardIds = rankedAwardIds.stream().limit(CANDIDATE_AWARD_NUM).toList();
            List<StandardAward> standardAwards = standardAwardMapper.getAwardsByBatchId(candidateAwardIds);

            logger.info("开始调用AI进行奖项分类，submissionId: {}", submissionId);
            AwardClassification awardClassification =classificationAgent.classifyAward(awardInfo,standardAwards);
            logger.info("AI奖项分类成功，匹配结果: {}，submissionId: {}", awardClassification.getMatchFound(), submissionId);

            if(!awardClassification.getMatchFound()){
                logger.warn("AI未能匹配到任何标准奖项，拒绝处理，submissionId: {}", submissionId);
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId",completeUploadInfo.get("submissionId"),
                        "status", AwardSubmissionStatus.AI_REJECTED,
                        "ocrFullText",awardInfo.toMap(),
                        "rejectionReason","没有匹配的奖项"));
            }

            List<AwardSubmission> awardSubmissions = awardSubmissionMapper.getSubmissionByMatchedAwardId(
                    awardClassification.getMatchedAwardId(),
                    completeUploadInfo.get("studentId").toString()
            );

            logger.info("开始调用AI进行重复性检查，submissionId: {}", submissionId);
            DeduplicationResult deduplicationResult = classificationAgent.checkForDuplicate(awardInfo,awardSubmissions);
            logger.info("AI重复性检查成功，检查结果: {}，submissionId: {}", deduplicationResult.getDuplicated(), submissionId);

            if(deduplicationResult.getDuplicated()){
                logger.warn("AI检测到重复提交，拒绝处理，submissionId: {}", submissionId);
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId",completeUploadInfo.get("submissionId"),
                        "status", AwardSubmissionStatus.AI_REJECTED,
                        "ocrFullText",awardInfo.toMap(),
                        "duplicateCheckResult",deduplicationResult.getDuplicated(),
                        "rejectionReason",deduplicationResult.getReasoning()));
            } else{
                logger.info("AI审核通过，更新状态为AI_APPROVED，submissionId: {}", submissionId);
                List<Map<String, Object>> standardAwardList = standardAwards.stream().map(StandardAward::toMap).toList();
                awardSubmissionMapper.updateAwardSubmission(Map.of(
                        "submissionId",completeUploadInfo.get("submissionId"),
                        "status", AwardSubmissionStatus.AI_APPROVED,
                        "ocrFullText",awardInfo.toMap(),
                        "matchedAwardId",awardClassification.getMatchedAwardId(),
                        "aiSuggestion","可能匹配的奖项:\n"+standardAwards));
            }
        }
    }
}
