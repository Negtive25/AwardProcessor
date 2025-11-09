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
    private LocalDate awardDate;

    private String ifCertification;
    private String isReadable;


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if(this.studentName != null){
            map.put("studentName", studentName);
        }
        if(this.awardName != null){
            map.put("awardName", awardName);
        }
        if(this.awardDate != null){
            map.put("awardDate", awardDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return map;
    }
}
