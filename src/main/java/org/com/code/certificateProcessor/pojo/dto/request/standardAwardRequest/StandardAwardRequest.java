package org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.AtLeastOneIsValid;
import org.com.code.certificateProcessor.pojo.validation.group.CreateGroup;
import org.com.code.certificateProcessor.pojo.validation.group.GetGroup;
import org.com.code.certificateProcessor.pojo.validation.group.UpdateGroup;
import org.com.code.certificateProcessor.util.serializer.BooleanFromEnumDeserializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@AtLeastOneIsValid(
        fieldNames = { "name", "category", "level","score","isActive"},
        message = "至少一个字段满足约束条件",
        groups = {UpdateGroup.class}
)
public class StandardAwardRequest {

    @JsonView(value = {UpdateGroup.class,GetGroup.class})
    @NotBlank(groups = {UpdateGroup.class})
    private String standardAwardId;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @Size(min = 2, max = 200, message = "奖状名字长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String name;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @Size(min = 2, max = 200, message = "奖状类别长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String category;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @Size(min = 2, max = 200, message = "奖状等级长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String level;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @Positive(message = "奖状分数必须大于0",groups = {CreateGroup.class,UpdateGroup.class,GetGroup.class})
    @NotNull(groups = {CreateGroup.class})
    private Double score;

    @JsonDeserialize(using = BooleanFromEnumDeserializer.class)
    @JsonView(value = {UpdateGroup.class,GetGroup.class})
    private Boolean isActive;

    @JsonView(value = {GetGroup.class})
    private String createdBy;

    @JsonView(value = {GetGroup.class})
    private String updatedBy;
}
