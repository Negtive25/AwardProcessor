package org.com.code.certificateProcessor.pojo.dto.request.adminRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.com.code.certificateProcessor.pojo.validation.ValidEnum;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminAuthRequest {
    @NotBlank(message = "提交 username 不能为空")
    private String username;

    @ValidEnum(enumClass = UpdateAdminAuthRequest.UpdateAuth.class)
    @NonNull
    private String auth;

    public enum UpdateAuth {
        ADMIN,
        DISABLE
    }
}
