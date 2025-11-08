package org.com.code.certificateProcessor.pojo.structMap;

import org.com.code.certificateProcessor.pojo.dto.response.awardSubmissionResponse.AdminAwardSubmissionResponse;
import org.com.code.certificateProcessor.pojo.dto.response.awardSubmissionResponse.BaseAwardSubmissionResponse;
import org.com.code.certificateProcessor.pojo.entity.AwardSubmission;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AwardSubmissionStructMap {

    @Named("toAdminAwardSubmissionResponse")
    @Mapping(source = "imageObjectKey", target = "temporaryImageURL")
    AdminAwardSubmissionResponse toAdminAwardSubmissionResponse(AwardSubmission awardSubmission);
    @IterableMapping(qualifiedByName = "toAdminAwardSubmissionResponse")
    List<AdminAwardSubmissionResponse> toAdminAwardSubmissionResponseList(List<AwardSubmission> awardSubmissionList);

    @Mapping(source = "imageObjectKey", target = "temporaryImageURL")
    @Named("toBaseAwardSubmissionResponse")
    BaseAwardSubmissionResponse toBaseAwardSubmissionResponse(AwardSubmission awardSubmission);
    @IterableMapping(qualifiedByName = "toBaseAwardSubmissionResponse")
    List<BaseAwardSubmissionResponse> toBaseAwardSubmissionResponseList(List<AwardSubmission> awardSubmissionList);
}
