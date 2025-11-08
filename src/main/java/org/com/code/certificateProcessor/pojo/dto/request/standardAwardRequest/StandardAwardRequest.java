package org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.group.CreateGroup;
import org.com.code.certificateProcessor.pojo.validation.group.UpdateGroup;
import org.com.code.certificateProcessor.util.serializer.BooleanFromEnumDeserializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class StandardAwardRequest {

    @JsonView(value = {UpdateGroup.class})
    @NotBlank(groups = {UpdateGroup.class})
    private String standardAwardId;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Size(min = 2, max = 200, message = "奖状名字长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class,UpdateGroup.class})
    private String name;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Size(min = 2, max = 200, message = "奖状类别长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class,UpdateGroup.class})
    private String category;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Size(min = 2, max = 200, message = "奖状等级长度必须在 2 到 200 之间",groups = {CreateGroup.class,UpdateGroup.class})
    @NotBlank(groups = {CreateGroup.class,UpdateGroup.class})
    private String level;

    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Positive(message = "奖状分数必须大于0",groups = {CreateGroup.class,UpdateGroup.class})
    @NotNull(groups = {CreateGroup.class,UpdateGroup.class})
    private Double score;

    @JsonDeserialize(using = BooleanFromEnumDeserializer.class)
    @JsonView(value = {UpdateGroup.class})
    @NotNull(groups = {UpdateGroup.class})
    private Boolean isActive;

}
