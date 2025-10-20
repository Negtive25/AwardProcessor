package org.com.code.certificateProcessor.pojo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStandardAward {
    @NotBlank(message = "standardAwardId不能为空")
    private String standardAwardId;
    private String name;
    private String category;
    private String level;
    private Double score;
}
