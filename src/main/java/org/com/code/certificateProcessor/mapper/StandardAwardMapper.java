package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.StandardAward;

import java.util.List;
import java.util.Map;

@Mapper
public interface StandardAwardMapper {
    List<StandardAward> getLatterStandardAward(String lastId, Integer pageSize, String status);
    List<StandardAward> getPreviousStandardAward(String lastId, Integer pageSize, String status);

    int addBatchStandardAward(List<Map<String, Object>> standardAwardList);
    int updateBatchStandardAward(List<Map<String, Object>> standardAwardList);
    int deleteStandardAward(String standardAwardId);
    List<StandardAward> getAwardsByBatchId(List<String> batchIds);
}
