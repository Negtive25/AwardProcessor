package org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.validation.group.GetGroup;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CursorFilteredQueryStandardAwardRequest {
    @Valid
    StandardAwardRequest standardAwardRequest;
    @Valid
    @NotNull(message = "cursorPageRequest 不能为空")
    CursorPageRequest cursorPageRequest;
}
