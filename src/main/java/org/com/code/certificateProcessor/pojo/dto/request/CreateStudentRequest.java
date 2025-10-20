package org.com.code.certificateProcessor.pojo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.com.code.certificateProcessor.validation.ValidPassword;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {
    @NotBlank(message = "学号不能为空")
    @Size(min = 9, max = 9, message = "学号长度必须为9位")
    private String studentId;

    @org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间")
    @ValidPassword
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "学院不能为空")
    private String college;

    @NotBlank(message = "专业不能为空")
    private String major;
}
