package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;

import java.util.List;

@Mapper
public interface StandardAwardMapper {
    StandardAward getStandardAwardById(String standardAwardId);

    List<StandardAward> getLatterStandardAward(String lastId, Integer pageSize);
    List<StandardAward> getPreviousStandardAward(String lastId, Integer pageSize);

    int addStandardAward(StandardAward standardAward);
    int updateStandardAward(StandardAward standardAward);
    int deleteStandardAward(String standardAwardId);
    List<StandardAward> getAwardsByBatchId(List<String> batchIds);
}
