package org.com.code.certificateProcessor.service.admin.impl;

import org.com.code.certificateProcessor.exeption.AdminException;
import org.com.code.certificateProcessor.mapper.AdminMapper;
import org.com.code.certificateProcessor.pojo.Admin;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.security.CustomAuthenticationToken;
import org.com.code.certificateProcessor.service.JWTService;
import org.com.code.certificateProcessor.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void addAdmin(Admin admin) {
        try {
            admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
            int result = adminMapper.addAdmin(admin);
            if (result == 0) {
                throw new AdminException("添加管理员失败");
            }
        }catch (Exception e){
            throw new AdminException("数据库异常，添加管理员失败",e);
        }
    }

    @Override
    public String adminSignIn(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new CustomAuthenticationToken(username, password, Auth.ADMIN.getType())
            );

            if (!authentication.isAuthenticated()) {
                throw new AdminException("用户名或密码错误");
            }
            return jwtService.getJwtToken(username, Auth.ADMIN.getType());
        }catch (Exception e) {
            throw new AdminException("管理员登录失败",e);
        }
    }

    @Override
    public Admin getAdminByUserName(String username) {
        try {
            return adminMapper.getAdminByUserName(username);
        }catch (Exception e) {
            throw new AdminException("数据库异常，获取管理员信息失败",e);
        }
    }
}
