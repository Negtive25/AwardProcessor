package org.com.code.certificateProcessor.pojo.dto.response.adminResponse;

import lombok.*;
import org.com.code.certificateProcessor.pojo.entity.Admin;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignInResponse{
    private String username;
    private String auth;
    private String token;
}
