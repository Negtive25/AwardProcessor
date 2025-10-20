package org.com.code.certificateProcessor.service.admin;

import org.com.code.certificateProcessor.pojo.Admin;
import org.com.code.certificateProcessor.pojo.AwardSubmission;

import java.util.List;

public interface AdminService {
    void addAdmin(Admin admin);
    String adminSignIn(String username, String password);
    Admin getAdminByUserName(String username);
}
