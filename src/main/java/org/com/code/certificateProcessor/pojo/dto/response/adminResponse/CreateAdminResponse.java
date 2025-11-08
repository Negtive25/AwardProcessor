package org.com.code.certificateProcessor.pojo.dto.response.adminResponse;

import lombok.*;
import org.com.code.certificateProcessor.pojo.entity.Admin;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminResponse{
    private String username;
    private String fullName;
    private String auth;
}
