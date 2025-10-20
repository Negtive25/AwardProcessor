package org.com.code.certificateProcessor.service.standardAward;

import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;
import java.util.Map;

public interface StandardAwardService {
    CursorPageResponse<StandardAward> cursorQueryStandardAward(String lastId, int pageSize);
    void addBatchStandardAward(List<Map<String, Object>> standardAwardList);
    void updateBatchStandardAward(List<Map<String, Object>> standardAwardList);
    void deleteStandardAward(String standardAwardId);
}
