package org.com.code.certificateProcessor.service.awardSubmission;

import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.dto.request.ReviewSubmissionRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;

public interface AwardSubmissionService {
    void revokeSubmission(String submissionId,String studentId);
    List<AwardSubmission> getAllSubmissionProgress(String studentId);
    CursorPageResponse<AwardSubmission> cursorQuerySubmissionByStatus(String lastStrId, int pageSize, String status);
    void ReviewSubmissionRequest(ReviewSubmissionRequest request);

    List<Double> sumApprovedScoreByStudentId(List<String> studentIds);
}
