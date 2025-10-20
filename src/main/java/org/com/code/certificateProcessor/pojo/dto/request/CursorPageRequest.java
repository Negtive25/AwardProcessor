package org.com.code.certificateProcessor.pojo.dto.request;

import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageRequest {
    /**
     * lastId 初始为 0
     */
    @NotBlank(message = "lastId不能为空")
    private String lastId;
    @NonNull
    private Integer pageSize; // 每页条数,正数代表往后翻页，负数代表往前翻页
}
