package org.com.code.certificateProcessor.service.student.impl;

import org.com.code.certificateProcessor.exeption.AdminException;
import org.com.code.certificateProcessor.exeption.StudentException;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.mapper.StudentMapper;
import org.com.code.certificateProcessor.pojo.AwardSubmission;
import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.security.CustomAuthenticationToken;
import org.com.code.certificateProcessor.service.BaseCursorPageService;
import org.com.code.certificateProcessor.service.JWTService;
import org.com.code.certificateProcessor.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentImpl extends BaseCursorPageService<Student> implements StudentService{
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private AwardSubmissionMapper awardSubmissionMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void addStudent(Student student) {
        try {
            student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
            int result = studentMapper.addStudent(student);
            if (result == 0) {
                throw new StudentException("添加学生失败，未插入任何记录");
            }
        }catch (Exception e){
            throw new StudentException("数据库异常，添加学生失败",e );
        }
    }

    @Override
    public String studentSignIn(String studentId, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new CustomAuthenticationToken(studentId, password, Auth.STUDENT.getType())
            );

            if (!authentication.isAuthenticated()) {
                throw new StudentException("用户名或密码错误");
            }
            return jwtService.getJwtToken(studentId, Auth.STUDENT.getType());
        }catch (Exception e) {
            throw new StudentException("用户登录失败",e);
        }
    }

    @Override
    public Student getStudentById(String studentId) {
        try {
            return studentMapper.getStudentById(studentId);
        }catch (Exception e) {
            throw new StudentException("数据库异常，获取学生信息失败",e);
        }
    }

    @Override
    public CursorPageResponse<Student> cursorQueryStudent(String lastStrId, int pageSize, String condition) {
        try {
            if(pageSize < 0){
                CursorPageResponse<Student> studentList =  fetchPage(lastStrId, - pageSize, studentMapper::getPreviousStudent, Student::getStudentId,condition);
                List<String> studentIds = studentList.getList().stream().map(Student::getStudentId).toList();
                List<Double> sumOfScoreList = awardSubmissionMapper.sumApprovedScoreByStudentIdList(studentIds);
                for (int i = 0; i < studentList.getList().size(); i++) {
                    studentList.getList().get(i).setSumOfScore(sumOfScoreList.get(i));
                }
                return studentList;
            }else{
                CursorPageResponse<Student> studentList =  fetchPage(lastStrId, pageSize, studentMapper::getLatterStudent, Student::getStudentId,condition);
                List<String> studentIds = studentList.getList().stream().map(Student::getStudentId).toList();
                List<Double> sumOfScoreList = awardSubmissionMapper.sumApprovedScoreByStudentIdList(studentIds);
                for (int i = 0; i < studentList.getList().size(); i++) {
                    studentList.getList().get(i).setSumOfScore(sumOfScoreList.get(i));
                }
                return studentList;
            }
        }catch (Exception e) {
            throw new StudentException("数据库异常，获取学生信息失败",e);
        }
    }
}
