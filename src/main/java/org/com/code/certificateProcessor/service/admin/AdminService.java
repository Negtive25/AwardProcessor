package org.com.code.certificateProcessor.service.admin;

import org.com.code.certificateProcessor.pojo.dto.request.adminRequest.UpdateAdminAuthRequest;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.AdminInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.request.adminRequest.AdminRequest;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.AdminSignInResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.CreateAdminResponse;

public interface AdminService {
    CreateAdminResponse addAdmin(AdminRequest adminRequest);
    AdminSignInResponse adminSignIn(String username, String password);
    AdminInfoResponse getAdminByUserName(String username);
    void updateAdminInfo(AdminRequest adminRequest);
    CursorPageResponse<AdminInfoResponse> cursorQueryAdmin(CursorPageRequest cursorPageRequest);

    void updateAdminAuth(UpdateAdminAuthRequest updateAdminAuthRequest);
}
