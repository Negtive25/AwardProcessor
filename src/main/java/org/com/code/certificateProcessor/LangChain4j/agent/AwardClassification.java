package org.com.code.certificateProcessor.LangChain4j.agent;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AwardClassification {
    private Boolean matchFound;
    private String matchedAwardId;
    private String matchedAwardName;
    private String reasoning;
}
