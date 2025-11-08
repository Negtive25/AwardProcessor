package org.com.code.certificateProcessor.pojo.modelInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private Long awardScore;
    private LocalDate awardDate;
    private String reason;
    private String ifCertification;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if(this.studentName != null){
            map.put("studentName", studentName);
        }
        if(this.awardName != null){
            map.put("awardName", awardName);
        }
        if(this.awardCategory != null){
            map.put("awardCategory", awardCategory);
        }
        if(this.awardLevel != null){
            map.put("awardLevel", awardLevel);
        }
        if(this.awardScore != null){
            map.put("awardScore", awardScore);
        }
        if(this.awardDate != null){
            map.put("awardDate", awardDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if(this.reason != null){
            map.put("reason", reason);
        }
        return map;
    }
}
