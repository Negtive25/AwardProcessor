package org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminStandardAwardInfoResponse extends BaseStandardAwardInfoResponse{
    private Boolean isActive;
}
