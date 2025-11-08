package org.com.code.certificateProcessor.pojo.dto.response.awardSubmissionResponse;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminAwardSubmissionResponse extends BaseAwardSubmissionResponse{
    private List<Map<String, Object>> suggestion;
}
