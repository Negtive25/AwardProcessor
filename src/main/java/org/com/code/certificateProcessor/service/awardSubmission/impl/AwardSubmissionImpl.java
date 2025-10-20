package org.com.code.certificateProcessor.service.awardSubmission.impl;

import org.com.code.certificateProcessor.exeption.StudentException;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.dto.request.ReviewSubmissionRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.service.BaseCursorPageService;
import org.com.code.certificateProcessor.service.awardSubmission.AwardSubmissionService;
import org.com.code.certificateProcessor.service.file.FileManageService;
import org.com.code.certificateProcessor.service.file.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AwardSubmissionImpl extends BaseCursorPageService<AwardSubmission> implements AwardSubmissionService {
    @Autowired
    private AwardSubmissionMapper awardSubmissionMapper;
    @Autowired
    private OSSService ossService;
    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;

    @Override
    public void revokeSubmission(String submissionId,String studentId) {
        try {
            int result = awardSubmissionMapper.deleteAwardSubmission(submissionId,studentId);
            /**
             *  <delete id="deleteAwardSubmission">
             *         delete from award_submission
             *         where submissionId = #{submissionId}
             *         and studentId = #{studentId} and status = 'AI_PROCESSING'
             *  </delete>
             */
            if(result == 0)
                throw new StudentException("撤销失败");

            String imageURL = awardSubmissionMapper.getSubmissionImageURL(submissionId,studentId);
            ossService.deleteFile(imageURL);

            objectRedisTemplate.opsForHash().put(FileManageService.IfSubmissionGotRevoked,submissionId,"1");
        }catch (Exception e) {
            throw new StudentException("数据库异常，撤销提交失败",e);
        }
    }

    @Override
    public List<AwardSubmission> getAllSubmissionProgress(String studentId) {
        try {
            return awardSubmissionMapper.getAllSubmission(studentId);
        }catch (Exception e) {
            throw new StudentException("数据库异常，获取学生提交进度失败",e);
        }
    }

    @Override
    public CursorPageResponse<AwardSubmission> cursorQuerySubmissionByStatus(String lastStrId, int pageSize, String status) {
        if(pageSize < 0)
            return fetchPage(lastStrId, - pageSize, awardSubmissionMapper::getPreviousSubmission, AwardSubmission::getSubmissionId,status);
        return fetchPage(lastStrId, pageSize, awardSubmissionMapper::getLatterSubmission, AwardSubmission::getSubmissionId,status);
    }

    @Override
    public void ReviewSubmissionRequest(ReviewSubmissionRequest request) {
        if(request.getUpdateStatus() == ReviewSubmissionRequest.UpdateStatus.MANUAL_APPROVED){
            awardSubmissionMapper.updateAwardSubmission(Map.of(
                    "submissionId",request.getSubmissionId(),
                    "status",AwardSubmissionStatus.MANUAL_APPROVED,
                    "reviewedBy",SecurityContextHolder.getContext().getAuthentication().getName(),
                    "rejectionReason",null
            ));
        }else if(request.getUpdateStatus() == ReviewSubmissionRequest.UpdateStatus.MANUAL_REJECTED){
            awardSubmissionMapper.updateAwardSubmission(Map.of(
                    "submissionId",request.getSubmissionId(),
                    "status",AwardSubmissionStatus.MANUAL_REJECTED,
                    "rejectionReason",request.getUpdateRejectionReason()
            ));
        }
    }

    @Override
    public List<Double> sumApprovedScoreByStudentId(List<String> studentIds) {
        try {
            return awardSubmissionMapper.sumApprovedScoreByStudentIdList(studentIds);
        }catch (Exception e) {
            throw new StudentException("数据库异常，获取学生提交进度失败",e);
        }
    }
}
