package org.com.code.certificateProcessor.pojo.dto.request;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.CreateGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.UpdateGroup;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StandardAwardRequest {

    @JsonView(value = {UpdateGroup.class})
    @NotBlank(message = "StandardAwardId不能为空",groups = {UpdateGroup.class})
    private String standardAwardId;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "奖状名字不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 1, max = 200, message = "奖状名字长度必须在 2 到 200 之间",groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "奖状类别不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 1, max = 50, message = "奖状类别长度必须在 2 到 200 之间",groups = {CreateGroup.class, UpdateGroup.class})
    private String category;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "奖状等级不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 1, max = 30, message = "奖状等级长度必须在 2 到 200 之间",groups = {CreateGroup.class, UpdateGroup.class})
    private String level;

    @JsonView(value = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(message = "奖状分数不能为空",groups = {CreateGroup.class, UpdateGroup.class})
    @DecimalMin(value = "0.0", inclusive = false, message = "奖状分数必须大于0",groups = {CreateGroup.class, UpdateGroup.class})
    private Double score;

    @JsonView(value = {UpdateGroup.class})
    private boolean isActive;

    private float[] nameVector;
}
