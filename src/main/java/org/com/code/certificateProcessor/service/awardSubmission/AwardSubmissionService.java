package org.com.code.certificateProcessor.service.awardSubmission;

import org.com.code.certificateProcessor.pojo.dto.StudentScoreDto;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.awardSubmissionResponse.BaseAwardSubmissionResponse;
import org.com.code.certificateProcessor.pojo.dto.request.ReviewSubmissionRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;

public interface AwardSubmissionService {
    void revokeSubmission(String submissionId,String studentId);

    CursorPageResponse<? extends BaseAwardSubmissionResponse> cursorQuerySubmissionByStatus(
            CursorPageRequest cursorPageRequest,
            List<String> status,
            Boolean isAdmin,
            String studentId
            );
    void reviewSubmissionRequest(ReviewSubmissionRequest request);

    Double sumApprovedScoreByStudentId(String studentIds);
}
