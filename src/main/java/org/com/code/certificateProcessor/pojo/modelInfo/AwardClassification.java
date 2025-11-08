package org.com.code.certificateProcessor.pojo.modelInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardClassification {
    private Boolean matchFound;
    private String matchedAwardId;
    private String matchedAwardName;
    private String reasoning;
}
