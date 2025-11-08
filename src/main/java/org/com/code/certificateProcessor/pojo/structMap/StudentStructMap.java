package org.com.code.certificateProcessor.pojo.structMap;

import org.com.code.certificateProcessor.pojo.dto.request.StudentRequest;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.CreateStudentResponse;
import org.com.code.certificateProcessor.pojo.dto.response.studentResponse.StudentInfoResponse;
import org.com.code.certificateProcessor.pojo.entity.Student;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentStructMap {
    Student toStudent(StudentRequest studentRequest);

    @Named("toStudentInfoResponse")
    StudentInfoResponse toStudentInfoResponse(Student student);
    @IterableMapping(qualifiedByName = "toStudentInfoResponse")
    List<StudentInfoResponse> toStudentInfoResponseList(List<Student> studentList);

    CreateStudentResponse toCreateStudentResponse(Student student);
}
