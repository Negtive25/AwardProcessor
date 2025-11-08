package org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseStandardAwardInfoResponse {
    protected String standardAwardId;
    protected String name;
    protected String category;
    protected String level;
    protected Double score;
    protected String createdBy;
    protected String updatedBy;
    protected LocalDate createdAt;
    protected LocalDate updatedAt;
}
