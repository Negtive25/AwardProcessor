package org.com.code.certificateProcessor.pojo.dto.request;

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
        fieldNames = {"password","college","major"},
        message = "更新时候 password,college,major 中至少要提供一项",
        groups = UpdateGroup.class
)
public class StudentRequest {
    @JsonView(value = {CreateGroup.class, SignInGroup.class})
    @Size(min = 9, max = 9, message = "学号长度必须为9位",groups = {CreateGroup.class, SignInGroup.class})
    @NotBlank(groups = {CreateGroup.class,SignInGroup.class})
    private String studentId;

    @JsonView(value = {CreateGroup.class, SignInGroup.class,UpdateGroup.class})
    @Size(min = 6,max = 20, message = "密码长度必须在 6 到 20 之间",groups = {CreateGroup.class,UpdateGroup.class})
    @ValidPassword(groups = {CreateGroup.class,UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class,SignInGroup.class})
    private String password;

    /**
     * 学生名字和奖状名字挂钩,不能更新
     */
    @JsonView(value = {CreateGroup.class})
    @Size(min = 2, max = 20, message = "姓名长度必须在 2 到 20 之间",groups = {CreateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String name;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Size(min = 2, max = 20, message = "学院长度必须在 2 到 20 之间",groups = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String college;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 2, max = 20, message = "专业长度必须在 2 到 20 之间",groups = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String major;
}
