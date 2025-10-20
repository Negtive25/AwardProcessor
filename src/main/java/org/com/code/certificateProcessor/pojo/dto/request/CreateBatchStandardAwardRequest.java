package org.com.code.certificateProcessor.pojo.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateBatchStandardAwardRequest {
    private List<CreateStandardAward> standardAwardRequestList;
}
