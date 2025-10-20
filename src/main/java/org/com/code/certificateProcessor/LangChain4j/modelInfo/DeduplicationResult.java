package org.com.code.certificateProcessor.LangChain4j.modelInfo;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeduplicationResult {
    private Boolean duplicated;
    private String matchedAwardId;
    private String matchedAwardName;
    private String reasoning;
}
