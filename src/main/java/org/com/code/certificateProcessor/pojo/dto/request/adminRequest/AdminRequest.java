package org.com.code.certificateProcessor.pojo.dto.request.adminRequest;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.AtLeastOneIsValid;
import org.com.code.certificateProcessor.pojo.validation.group.CreateGroup;
import org.com.code.certificateProcessor.pojo.validation.group.SignInGroup;
import org.com.code.certificateProcessor.pojo.validation.group.UpdateGroup;
import org.com.code.certificateProcessor.pojo.validation.ValidPassword;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@AtLeastOneIsValid(
        fieldNames = {"password", "fullName"},
        message = "更新时候 password, fullName 中至少要提供一项",
        groups = UpdateGroup.class
)
public class AdminRequest {

    @JsonView(value = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @Size(min = 2, max = 20, message = "用户名长度必须在 2 到 20 之间",groups = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class, SignInGroup.class})
    private String username;

    @JsonView(value = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间",groups = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @ValidPassword(groups = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class, SignInGroup.class})
    private String password;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 2, max = 20, message = "姓名长度必须在 2 到 20 之间",groups = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String fullName;
}
