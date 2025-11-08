package org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.group.DeleteGroup;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStandardAwardListRequest {
    @JsonView(value = {DeleteGroup.class})
    @NotEmpty(message="请求列表不能为空", groups = DeleteGroup.class)
    @Valid
    private List<@NotBlank(message = "ID 字符串不能为空", groups = DeleteGroup.class)
            String> standardAwardIdList;
}
