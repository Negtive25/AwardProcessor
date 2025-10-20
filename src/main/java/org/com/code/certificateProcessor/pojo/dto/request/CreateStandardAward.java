package org.com.code.certificateProcessor.pojo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateStandardAward {
    @NotBlank(message = "奖状名字不能为空")
    @Size(min = 1, max = 200, message = "奖状名字长度必须在 2 到 200 之间")
    private String name;

    @NotBlank(message = "奖状类别不能为空")
    @Size(min = 1, max = 50, message = "奖状类别长度必须在 2 到 200 之间")
    private String category;
    @NotBlank(message = "奖状等级不能为空")
    @Size(min = 1, max = 30, message = "奖状等级长度必须在 2 到 200 之间")
    private String level;
    @NotBlank(message = "奖状分数不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "奖状分数必须大于0")
    private Double score;
}
