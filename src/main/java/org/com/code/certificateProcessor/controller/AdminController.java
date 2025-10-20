package org.com.code.certificateProcessor.controller;

import jakarta.validation.Valid;
import org.com.code.certificateProcessor.pojo.Admin;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.request.CreateAdminRequest;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.request.ReviewSubmissionRequest;
import org.com.code.certificateProcessor.pojo.dto.request.SignInAdminRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.com.code.certificateProcessor.service.admin.AdminService;
import org.com.code.certificateProcessor.service.awardSubmission.AwardSubmissionService;
import org.com.code.certificateProcessor.service.standardAward.StandardAwardService;
import org.com.code.certificateProcessor.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    AwardSubmissionService awardSubmissionService;
    @Autowired
    private StudentService studentService;

    @PostMapping("/signUp")
    public ResponseHandler signUp(
            // 这个 @Valid 是必须的，它负责触发上面 DTO 中所有规则的检查
            @Valid @RequestBody CreateAdminRequest createAdminRequest) {
        Admin admin = new Admin();
        admin.setUsername(createAdminRequest.getUsername());
        admin.setPassword(createAdminRequest.getPassword());
        admin.setFullName(createAdminRequest.getFullName());
        admin.setAuth(Auth.ADMIN.getType());
        adminService.addAdmin(admin);
        return new ResponseHandler(ResponseHandler.SUCCESS, "注册成功");
    }
    @PostMapping("/signIn")
    public ResponseHandler signIn(@Valid @RequestBody SignInAdminRequest signInAdminRequest) {
        String token = adminService.adminSignIn(signInAdminRequest.getUsername(),
                signInAdminRequest.getPassword());
        return new ResponseHandler(ResponseHandler.SUCCESS, "登录成功,获取token", token);
    }
    @GetMapping("/me")
    public ResponseHandler me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminService.getAdminByUserName(username);
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取当前用户信息成功", admin);
    }

    @GetMapping("/getSubmissionProgress")
    public ResponseHandler getSubmissionProgress(@Valid @RequestBody CursorPageRequest cursorPageRequest,@RequestParam String status) {
        CursorPageResponse<AwardSubmission> submissionProgress = awardSubmissionService.cursorQuerySubmissionByStatus(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize(), status);
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取"+status+"进度成功", submissionProgress);
    }

    @PutMapping("/reviewSubmission")
    public ResponseHandler reviewSubmission(@Valid @RequestBody ReviewSubmissionRequest reviewSubmissionRequest) {
        awardSubmissionService.ReviewSubmissionRequest(reviewSubmissionRequest);
        return new ResponseHandler(ResponseHandler.SUCCESS, "更新提交状态成功");
    }

    @GetMapping("/getStudentInfo")
    public ResponseHandler getStudentInfo(@Valid @RequestBody CursorPageRequest cursorPageRequest) {
        CursorPageResponse<Student> submissionProgress = studentService.cursorQueryStudent(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize(), null);
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取学生信息成功", submissionProgress);
    }
}
