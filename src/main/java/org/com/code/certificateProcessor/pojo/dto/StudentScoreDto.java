package org.com.code.certificateProcessor.pojo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@NoArgsConstructor
public class StudentScoreDto {
    private String studentId;
    private Double sumOfScore;
}
