package org.com.code.certificateProcessor.pojo.dto.request;

import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;
import org.com.code.certificateProcessor.validation.ValidEnum;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubmissionRequest {
    @NotBlank(message = "提交ID不能为空")
    private String submissionId;

    @ValidEnum(enumClass = UpdateStatus.class)
    private UpdateStatus updateStatus;

    private String updateRejectionReason;

    public enum UpdateStatus {
        MANUAL_APPROVED, MANUAL_REJECTED
    }
}
