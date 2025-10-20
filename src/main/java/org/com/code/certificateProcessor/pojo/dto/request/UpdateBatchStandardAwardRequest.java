package org.com.code.certificateProcessor.pojo.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBatchStandardAwardRequest {
    private List<UpdateStandardAward> updateStandardAwardRequestList;
}
