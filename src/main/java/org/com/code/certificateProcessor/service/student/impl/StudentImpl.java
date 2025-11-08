package org.com.code.certificateProcessor.service.student.impl;

import org.com.code.certificateProcessor.exeption.ResourceNotFoundException;
import org.com.code.certificateProcessor.exeption.StudentTableException;
import org.com.code.certificateProcessor.exeption.UnauthorizedException;
import org.com.code.certificateProcessor.mapper.AwardSubmissionMapper;
import org.com.code.certificateProcessor.mapper.StudentMapper;
import org.com.code.certificateProcessor.pojo.dto.StudentScoreDto;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.CreateStudentResponse;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.StudentInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.StudentSignInResponse;
import org.com.code.certificateProcessor.pojo.entity.Student;
import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.enums.Auth;
import org.com.code.certificateProcessor.pojo.structMap.StudentStructMap;
import org.com.code.certificateProcessor.security.CustomAuthenticationToken;
import org.com.code.certificateProcessor.service.BaseCursorPageService;
import org.com.code.certificateProcessor.service.JWTService;
import org.com.code.certificateProcessor.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private StudentStructMap studentStructMap;

    @Override
    @Transactional
    public CreateStudentResponse addStudent(StudentRequest studentRequest) {
        try {
            Student student = studentStructMap.toStudent(studentRequest);
            student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
            student.setAuth(Auth.STUDENT.getName());
            int result = studentMapper.addStudent(student);
            if (result == 0) {
                throw new StudentTableException("添加学生失败，未插入任何记录");
            }
            return studentStructMap.toCreateStudentResponse(student);
        }catch (StudentTableException e){
            throw e;
        }
    }

    @Override
    public StudentSignInResponse studentSignIn(String studentId, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new CustomAuthenticationToken(studentId, password, Auth.STUDENT.getName())
            );

            if (!authentication.isAuthenticated()) {
                throw new UnauthorizedException("用户名或密码错误");
            }
            String token = jwtService.getJwtToken(studentId, Auth.STUDENT.getName());

            return new StudentSignInResponse(studentId,authentication.getAuthorities().toArray()[0].toString(), token);
        }catch (UnauthorizedException e) {
            throw e;
        }
    }

    @Override
    public StudentInfoResponse getStudentById(String studentId) {
        try {
            Student student = studentMapper.getStudentById(studentId);
            if(student == null){
                throw new ResourceNotFoundException("学生信息不存在");
            }
            return studentStructMap.toStudentInfoResponse(student);
        }catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @Override
    public CursorPageResponse<StudentInfoResponse> cursorQueryStudent(CursorPageRequest cursorPageRequest) {
        String lastStrId = cursorPageRequest.getLastId();
        int pageSize = cursorPageRequest.getPageSize();

        try {
            CursorPageResponse<Student> studentList;
            if(pageSize < 0){
                studentList =  fetchPage(lastStrId, - pageSize, studentMapper::getPreviousStudent, Student::getStudentId);
            }else{
                studentList =  fetchPage(lastStrId, pageSize, studentMapper::getLatterStudent, Student::getStudentId);
            }

            List<String> studentIds = studentList.getList().stream().map(Student::getStudentId).toList();
            List<StudentScoreDto> sumOfScoreList = awardSubmissionMapper.sumApprovedScoreByStudentIdList(studentIds);
            Map<String, Double> scoreMap = sumOfScoreList.stream()
                    .collect(Collectors.toMap(
                            StudentScoreDto::getStudentId,
                            StudentScoreDto::getSumOfScore
                    ));

            for (int i = 0; i < studentIds.size(); i++) {
                Double score = scoreMap.getOrDefault(studentIds.get(i), 0.0);
                studentList.getList().get(i).setSumOfScore(score);
            }

            List<StudentInfoResponse> studentInfoResponseList = studentStructMap.toStudentInfoResponseList(studentList.getList());
            return new CursorPageResponse<>(
                    studentInfoResponseList,
                    studentList.getMinId(),
                    studentList.getMaxId(),
                    studentList.getHasNext()
            );
        }catch (Exception e) {
            throw new StudentTableException("数据库异常，获取学生信息失败",e);
        }
    }

    @Override
    public void updateStudentInfo(StudentRequest studentRequest){
        try {
            Student student = studentStructMap.toStudent(studentRequest);
            student.setStudentId(SecurityContextHolder.getContext().getAuthentication().getName());
            if(student.getPassword() != null && !student.getPassword().isEmpty())
                student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
            studentMapper.updateStudentInfo(student);
        }catch (Exception e) {
            throw new StudentTableException("数据库异常，更新学生信息失败",e);
        }
    }
}
