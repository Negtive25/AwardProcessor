package org.com.code.certificateProcessor.pojo.dto.response.studentResponse;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentSignInResponse{
    private String studentId;
    private String auth;
    private String token;
}
