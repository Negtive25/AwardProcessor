package org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.group.CreateGroup;
import org.com.code.certificateProcessor.pojo.validation.group.UpdateGroup;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateAndUpdateStandardAwardListRequest {
    @NotEmpty(message="请求列表不能为空", groups = {CreateGroup.class,UpdateGroup.class})
    @JsonView(value = {CreateGroup.class,UpdateGroup.class})
    @Valid
    private List<@NotNull StandardAwardRequest> requestList;
}
