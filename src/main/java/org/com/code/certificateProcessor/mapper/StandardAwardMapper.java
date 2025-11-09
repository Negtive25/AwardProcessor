package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;

import java.util.List;

@Mapper
public interface StandardAwardMapper {
    StandardAward getStandardAwardById(String standardAwardId);

    List<StandardAward> getLatterFilteredStandardAward(CursorPageRequest cursorPageRequest, StandardAward standardAward);
    List<StandardAward> getPreviousFilteredStandardAward(CursorPageRequest cursorPageRequest, StandardAward standardAward);

    int addStandardAward(StandardAward standardAward);
    int updateStandardAward(StandardAward standardAward);
    int deleteStandardAward(String standardAwardId);
    List<StandardAward> getAwardsByBatchId(List<String> batchIds);
}
