package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.request.StandardAwardRequest;

import java.util.List;
import java.util.Map;

@Mapper
public interface StandardAwardMapper {
    List<StandardAward> getLatterStandardAward(String lastId, Integer pageSize);
    List<StandardAward> getPreviousStandardAward(String lastId, Integer pageSize);

    int addBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    int updateBatchStandardAward(List<StandardAwardRequest> standardAwardList);
    int deleteStandardAward(String standardAwardId);
    List<StandardAward> getAwardsByBatchId(List<String> batchIds);
}
