package org.com.code.certificateProcessor.controller;

import jakarta.validation.Valid;
import org.checkerframework.common.value.qual.EnumVal;
import org.com.code.certificateProcessor.pojo.Admin;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.CreateGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.SignInGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.UpdateGroup;
import org.com.code.certificateProcessor.pojo.dto.request.*;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.com.code.certificateProcessor.service.admin.AdminService;
import org.com.code.certificateProcessor.service.awardSubmission.AwardSubmissionService;
import org.com.code.certificateProcessor.service.student.StudentService;
import org.com.code.certificateProcessor.validation.ValidEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    AwardSubmissionService awardSubmissionService;
    @Autowired
    private StudentService studentService;

    @PostMapping("/signUp")
    public ResponseHandler signUp(
            @Validated(CreateGroup.class) @RequestBody AdminRequest adminRequest) {
        Admin admin = new Admin();
        admin.setUsername(adminRequest.getUsername());
        admin.setPassword(adminRequest.getPassword());
        admin.setFullName(adminRequest.getFullName());
        admin.setAuth(Auth.ADMIN.getType());
        adminService.addAdmin(admin);
        return new ResponseHandler(ResponseHandler.SUCCESS, "注册成功");
    }
    @PostMapping("/signIn")
    public ResponseHandler signIn(@Validated(SignInGroup.class) @RequestBody AdminRequest adminRequest) {
        String token = adminService.adminSignIn(adminRequest.getUsername(),
                adminRequest.getPassword());
        return new ResponseHandler(ResponseHandler.SUCCESS, "登录成功,获取token", token);
    }
    @GetMapping("/me")
    public ResponseHandler me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminService.getAdminByUserName(username);
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取当前用户信息成功", admin);
    }

    /**
     * 获取提交进度
     * @param cursorPageRequest
     * @param status
     * @return
     */
    @GetMapping("/getSubmissionProgress")
    public ResponseHandler getSubmissionProgress(@Valid @RequestBody CursorPageRequest cursorPageRequest,
                                                 @RequestParam List<@ValidEnum(enumClass = AwardSubmissionStatus.class) String> status) {
        CursorPageResponse<AwardSubmission> submissionProgress =
                awardSubmissionService.cursorQuerySubmissionByStatus(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize(), null,status);
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取"+status+"进度成功", submissionProgress);
    }

    @PutMapping("/reviewSubmission")
    public ResponseHandler reviewSubmission(@Valid @RequestBody ReviewSubmissionRequest reviewSubmissionRequest) {
        awardSubmissionService.ReviewSubmissionRequest(reviewSubmissionRequest);
        return new ResponseHandler(ResponseHandler.SUCCESS, "更新提交状态成功");
    }

    @GetMapping("/getStudentInfo")
    public ResponseHandler getStudentInfo(@Valid @RequestBody CursorPageRequest cursorPageRequest) {
        CursorPageResponse<Student> submissionProgress = studentService.cursorQueryStudent(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize());
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取学生信息成功", submissionProgress);
    }

    @PutMapping("/updateInfo")
    public ResponseHandler updateInfo(@Validated(UpdateGroup.class) @RequestBody AdminRequest adminRequest) {

        return new ResponseHandler(ResponseHandler.SUCCESS, "更新学生信息成功");
    }

    
}
