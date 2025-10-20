package org.com.code.certificateProcessor.pojo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.com.code.certificateProcessor.validation.ValidPassword;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在 2 到 20 之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间")
    @ValidPassword
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String fullName;
}
