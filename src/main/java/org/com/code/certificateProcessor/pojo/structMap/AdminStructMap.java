package org.com.code.certificateProcessor.pojo.structMap;

import org.com.code.certificateProcessor.pojo.dto.request.adminRequest.AdminRequest;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.AdminInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.CreateAdminResponse;
import org.com.code.certificateProcessor.pojo.entity.Admin;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminStructMap {
    Admin toAdmin(AdminRequest adminRequest);

    CreateAdminResponse toCreateAdminResponse(Admin admin);

    @Named("toAdminInfoResponse")
    AdminInfoResponse toAdminInfoResponse(Admin admin);

    @IterableMapping(qualifiedByName = "toAdminInfoResponse")
    List<AdminInfoResponse> toAdminInfoResponseList(List<Admin> adminList);
}
