package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.Student;
import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    Student getStudentAuth(String studentId);
    int addStudent(Student student);

    String getStudentNameById(String studentId);
    Student getStudentById(String studentId);

    List<Student> getLatterStudent(String lastId, Integer pageSize);
    List<Student> getPreviousStudent(String lastId, Integer pageSize);

    int updateStudentInfo(StudentRequest studentRequest);
}
