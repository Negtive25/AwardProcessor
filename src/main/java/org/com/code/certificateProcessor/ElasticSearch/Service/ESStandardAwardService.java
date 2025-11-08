package org.com.code.certificateProcessor.ElasticSearch.Service;

import org.com.code.certificateProcessor.pojo.dto.document.StandardAwardDocument;

import java.io.IOException;
import java.util.List;

public interface ESStandardAwardService {
    /**
     * 创建标准奖状记录索引
     */
    void bulkCreateStandardAwardIndex(List<StandardAwardDocument> standardAwardDocumentList) throws IOException;

    /**
     * 删除标准奖状记录索引
     */
    void deleteStandardAwardIndex(List<String> standardAwardIdList);

    /**
     * 更新标准奖状记录索引
     */
    void updateStandardAwardIndex(List<StandardAwardDocument> standardAwardDocumentList);
}
