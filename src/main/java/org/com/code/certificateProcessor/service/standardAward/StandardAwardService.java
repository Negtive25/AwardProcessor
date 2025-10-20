package org.com.code.certificateProcessor.service.standardAward;

import java.util.List;
import java.util.Map;

public interface StandardAwardService {
    void addBatchStandardAward(List<Map<String, Object>> standardAwardList);
    void updateBatchStandardAward(List<Map<String, Object>> standardAwardList);
    void deleteStandardAward(String standardAwardId);
}
