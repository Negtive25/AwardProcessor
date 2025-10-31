package org.com.code.certificateProcessor.ElasticSearch.Service;

import org.com.code.certificateProcessor.pojo.dto.request.StandardAwardRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ESStandardAwardService {
    /**
     * 创建标准奖状记录索引
     */
    void bulkCreateStandardAwardIndex(List<StandardAwardRequest> standardAwardList) throws IOException;

    /**
     * 删除标准奖状记录索引
     */
    void deleteStandardAwardIndex(String standardAwardId);

    /**
     * 更新标准奖状记录索引
     */
    void updateStandardAwardIndex(List<StandardAwardRequest> standardAwardList);
}
