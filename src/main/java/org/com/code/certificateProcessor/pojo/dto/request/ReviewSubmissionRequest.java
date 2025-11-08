package org.com.code.certificateProcessor.pojo.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import org.com.code.certificateProcessor.pojo.validation.ValidEnum;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubmissionRequest {
    @NotBlank(message = "提交 ID 不能为空")
    private String submissionId;

    @ValidEnum(enumClass = UpdateStatus.class)
    @NonNull
    private String updateStatus;

    private String updateReason;

    private Double finalScore;

    public enum UpdateStatus {
        MANUAL_APPROVED, MANUAL_REJECTED
    }
}
