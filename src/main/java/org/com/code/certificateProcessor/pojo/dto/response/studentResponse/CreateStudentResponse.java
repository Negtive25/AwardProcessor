package org.com.code.certificateProcessor.pojo.dto.response.studentResponse;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentResponse{
    protected String studentId;
    protected String name;
    private String auth;
}
