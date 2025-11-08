package org.com.code.certificateProcessor.pojo.structMap;

import org.com.code.certificateProcessor.pojo.dto.document.StandardAwardDocument;
import org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest.StandardAwardRequest;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.AdminStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.BaseStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StandardAwardStructMap {

    @Named("toStandardAward")
    StandardAward toStandardAward(StandardAwardRequest standardAwardRequest);
    @IterableMapping(qualifiedByName = "toStandardAward")
    List<StandardAward> toStandardAwardList(List<StandardAwardRequest> standardAwardRequestList);


    @Named("toBaseStandardAwardInfoResponse")
    BaseStandardAwardInfoResponse toBaseStandardAwardInfoResponse(StandardAward standardAward);
    @IterableMapping(qualifiedByName = "toBaseStandardAwardInfoResponse")
    List<BaseStandardAwardInfoResponse> toBaseStandardAwardInfoResponseList(List<StandardAward> standardAwardList);


    @Named("toAdminStandardAwardInfoResponse")
    AdminStandardAwardInfoResponse toAdminStandardAwardInfoResponse(StandardAward standardAward);
    @IterableMapping(qualifiedByName = "toAdminStandardAwardInfoResponse")
    List<AdminStandardAwardInfoResponse> toAdminStandardAwardInfoResponseList(List<StandardAward> standardAwardList);

    @Named("toStandardAwardDocument")
    StandardAwardDocument toStandardAwardDocument(StandardAward standardAward);
    @IterableMapping(qualifiedByName = "toStandardAwardDocument")
    List<StandardAwardDocument> toStandardAwardDocumentList(List<StandardAward> standardAwardList);
}