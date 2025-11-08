package org.com.code.certificateProcessor.service.admin.impl;

import org.com.code.certificateProcessor.exeption.AdminTableException;
import org.com.code.certificateProcessor.exeption.ResourceNotFoundException;
import org.com.code.certificateProcessor.exeption.UnauthorizedException;
import org.com.code.certificateProcessor.mapper.AdminMapper;
import org.com.code.certificateProcessor.pojo.dto.request.adminRequest.UpdateAdminAuthRequest;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.AdminInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.AdminSignInResponse;
import org.com.code.certificateProcessor.pojo.dto.response.adminResponse.CreateAdminResponse;
import org.com.code.certificateProcessor.pojo.entity.Admin;
import org.com.code.certificateProcessor.pojo.dto.request.adminRequest.AdminRequest;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.pojo.structMap.AdminStructMap;
import org.com.code.certificateProcessor.security.CustomAuthenticationToken;
import org.com.code.certificateProcessor.service.BaseCursorPageService;
import org.com.code.certificateProcessor.service.JWTService;
import org.com.code.certificateProcessor.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminImpl extends BaseCursorPageService<Admin> implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    AdminStructMap adminStructMap;

    @Override
    @Transactional
    public CreateAdminResponse addAdmin(AdminRequest adminRequest) {
        try {
            Admin admin = adminStructMap.toAdmin(adminRequest);
            admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
            admin.setAuth(Auth.ADMIN.getName());
            int rowAffected = adminMapper.addAdmin(admin);
            if (rowAffected != 1) {
                throw new AdminTableException("添加管理员失败");
            }
            return adminStructMap.toCreateAdminResponse(admin);
        } catch (AdminTableException e) {
            throw e;
        }
    }

    @Override
    public AdminSignInResponse adminSignIn(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new CustomAuthenticationToken(username, password, Auth.ADMIN.getName())
            );

            if (!authentication.isAuthenticated()) {
                throw new UnauthorizedException("用户名或密码错误");
            }
            String token = jwtService.getJwtToken(username, authentication.getAuthorities().toArray()[0].toString());


            return new AdminSignInResponse(username, authentication.getAuthorities().toArray()[0].toString(), token);
        } catch (UnauthorizedException e) {
            throw e;
        }
    }

    @Override
    public AdminInfoResponse getAdminByUserName(String username) {
        try {
            Admin admin = adminMapper.getAdminByUserName(username);
            return adminStructMap.toAdminInfoResponse(admin);
        } catch (Exception e) {
            throw new ResourceNotFoundException("管理员信息不存在");
        }
    }

    @Override
    @Transactional
    public void updateAdminInfo(AdminRequest adminRequest) {
        try {
            Admin admin = adminStructMap.toAdmin(adminRequest);
            admin.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            if (admin.getPassword() != null && !admin.getPassword().isEmpty())
                admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
            adminMapper.updateAdminInfo(admin);
        } catch (Exception e) {
            throw new AdminTableException("数据库异常，更新管理员信息失败", e);
        }
    }

    @Override
    public CursorPageResponse<AdminInfoResponse> cursorQueryAdmin(CursorPageRequest cursorPageRequest) {
        String lastStrId = cursorPageRequest.getLastId();
        int pageSize = cursorPageRequest.getPageSize();
        try {
            CursorPageResponse<Admin> adminList;
            if(pageSize < 0){
                adminList =  fetchPage(lastStrId, - pageSize, adminMapper::getPreviousAdmin, Admin::getUsername);
            }else{
                adminList =  fetchPage(lastStrId, pageSize, adminMapper::getLatterAdmin, Admin::getUsername);
            }

            List<AdminInfoResponse> adminInfoResponses = adminStructMap.toAdminInfoResponseList(adminList.getList());
            return new CursorPageResponse<>(
                    adminInfoResponses,
                    adminList.getMinId(),
                    adminList.getMaxId(),
                    adminList.getHasNext()
            );

        } catch (Exception e) {
            throw new AdminTableException("数据库异常，获取管理员列表信息失败", e);
        }
    }

     @Override
    @Transactional
    public void updateAdminAuth(UpdateAdminAuthRequest updateAdminAuthRequest) {
        String username = updateAdminAuthRequest.getUsername();
        String auth = updateAdminAuthRequest.getAuth();
        try {
            int rowAffected = adminMapper.updateAdminAuth(username, auth);
            if (rowAffected != 1) {
                throw new AdminTableException("数据库异常，更新管理员权限失败");
            }
        } catch (AdminTableException e) {
                throw e;
        }
    }
}
