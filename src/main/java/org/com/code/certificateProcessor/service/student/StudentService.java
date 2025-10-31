package org.com.code.certificateProcessor.service.student;

import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;

public interface StudentService {
    void addStudent (Student student);
    String studentSignIn(String username, String password);
    Student getStudentById(String studentId);
    CursorPageResponse<Student> cursorQueryStudent(String lastStrId, int pageSize);
    void updateStudentInfo(StudentRequest studentRequest);
}
