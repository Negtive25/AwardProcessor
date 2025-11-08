package org.com.code.certificateProcessor.service.student;

import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.CreateStudentResponse;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.StudentInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.StudentSignInResponse;
import org.com.code.certificateProcessor.pojo.entity.Student;
import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

public interface StudentService {
    CreateStudentResponse addStudent (StudentRequest studentRequest);
    StudentSignInResponse studentSignIn(String username, String password);
    StudentInfoResponse getStudentById(String studentId);
    CursorPageResponse<StudentInfoResponse> cursorQueryStudent(CursorPageRequest cursorPageRequest);
    void updateStudentInfo(StudentRequest studentRequest);
}
