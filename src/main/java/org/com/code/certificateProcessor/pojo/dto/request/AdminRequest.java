package org.com.code.certificateProcessor.pojo.dto.request;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.CreateGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.SignInGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.UpdateGroup;
import org.com.code.certificateProcessor.validation.ValidPassword;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {

    @JsonView(value = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @NotBlank(message = "用户名不能为空",groups = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @Size(min = 2, max = 20, message = "用户名长度必须在 2 到 20 之间")
    private String username;

    @JsonView(value = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @NotBlank(message = "密码不能为空",groups = {CreateGroup.class,SignInGroup.class, UpdateGroup.class})
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间")
    @ValidPassword
    private String password;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "姓名不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    private String fullName;
}
