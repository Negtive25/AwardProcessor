package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.dto.StudentScoreDto;
import org.com.code.certificateProcessor.pojo.entity.AwardSubmission;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;

import java.util.List;

@Mapper
public interface AwardSubmissionMapper {
    AwardSubmissionStatus getSubmissionStatus(String submissionId);

    String getSubmissionImageObjectKey(String submissionId, String studentId);
    List<AwardSubmission> getSubmissionsForDuplicateCheck(String studentId, List<String> statusList);

    int addAwardSubmission(AwardSubmission awardSubmission);
    int deleteAwardSubmission(String submissionId,String studentId);

    int updateAwardSubmission(AwardSubmission awardSubmission);
    List<AwardSubmission> getLatterSubmission(String lastId, Integer pageSize,
                                              List<String> statusList,Boolean isAdmin,String studentId);
    List<AwardSubmission> getPreviousSubmission(String lastId, Integer pageSize,
                                                List<String> statusList,Boolean isAdmin,String studentId);

    List<StudentScoreDto> sumApprovedScoreByStudentIdList(List<String> studentIdList);
}
