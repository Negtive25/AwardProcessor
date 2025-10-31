package org.com.code.certificateProcessor.service.standardAward;

import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.request.StandardAwardRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;
import java.util.Map;

public interface StandardAwardService {
    CursorPageResponse<StandardAward> cursorQueryStandardAward(String lastId, int pageSize);
    void addBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    void updateBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    void deleteStandardAward(String standardAwardId);
}
