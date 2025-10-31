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
public class StudentRequest {
    @JsonView(value = {CreateGroup.class, SignInGroup.class})
    @NotBlank(message = "学号不能为空",groups = {CreateGroup.class, SignInGroup.class})
    @Size(min = 9, max = 9, message = "学号长度必须为9位",groups = {CreateGroup.class, SignInGroup.class})
    private String studentId;

    @JsonView(value = {CreateGroup.class, SignInGroup.class, UpdateGroup.class})
    @NotBlank(message = "密码不能为空",groups = {CreateGroup.class,SignInGroup.class, UpdateGroup.class})
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间",groups = {CreateGroup.class, UpdateGroup.class})
    @ValidPassword(groups = {CreateGroup.class, UpdateGroup.class})
    private String password;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "姓名不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "学院不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    private String college;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "专业不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    private String major;
}
