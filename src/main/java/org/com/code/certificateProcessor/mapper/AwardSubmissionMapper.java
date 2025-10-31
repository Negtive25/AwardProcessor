package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.AwardSubmission;

import java.util.List;
import java.util.Map;

@Mapper
public interface AwardSubmissionMapper {
    String getSubmissionImageObjectKey(String submissionId, String studentId);
    List<AwardSubmission> getApprovedSubmissionByStudentId(String studentId);

    int addAwardSubmission(Map<String, Object> params);
    int deleteAwardSubmission(String submissionId,String studentId);

    int updateAwardSubmission(Map<String, Object> params);
    List<AwardSubmission> getLatterSubmission(String lastId, Integer pageSize, String studentId,List<String> status);
    List<AwardSubmission> getPreviousSubmission(String lastId, Integer pageSize, String studentId,List<String> status);

    List<Double> sumApprovedScoreByStudentIdList(List<String> studentId);
}
