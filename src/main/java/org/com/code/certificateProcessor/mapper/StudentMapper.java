package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.Student;

import java.util.List;

@Mapper
public interface StudentMapper {
    Student getStudentAuth(String studentId);
    int addStudent(Student student);

    String getStudentNameById(String studentId);
    Student getStudentById(String studentId);

    List<Student> getLatterStudent(String lastId, Integer pageSize, String condition);
    List<Student> getPreviousStudent(String lastId, Integer pageSize, String condition);
}
