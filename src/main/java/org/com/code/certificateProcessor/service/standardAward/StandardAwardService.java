package org.com.code.certificateProcessor.service.standardAward;

import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.AdminStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.BaseStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest.StandardAwardRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;

public interface StandardAwardService {
    AdminStandardAwardInfoResponse getStandardAwardById(String standardAwardId);
    CursorPageResponse<? extends BaseStandardAwardInfoResponse> cursorQueryStandardAward(CursorPageRequest cursorPageRequest,String studentId);
    void addBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    void updateBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    void deleteStandardAward(List<String> standardAwardIdList);
}
