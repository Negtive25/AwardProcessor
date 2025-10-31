package org.com.code.certificateProcessor.LangChain4j.modelInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardInfo {
    private String studentName;
    private String awardName;
    private String awardCategory;
    private String awardLevel;
    private long awardScore;
    private LocalDate awardDate;
    private String ifCertification;

    public Map<String, Object> toMap() {
        return Map.of(
                "studentName", studentName,
                "awardName", awardName,
                "awardCategory", awardCategory,
                "awardLevel", awardLevel,
                "awardScore", awardScore,
                "awardDate", awardDate,
                "ifCertification", ifCertification
        );
    }
}
