package org.com.code.certificateProcessor.pojo.dto.response;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
    private List<T> list;     // 当前页数据
    private String minId;      // 这一页的最小id
    private String maxId;      // 这一页的最大id
    private Boolean hasNext;  // 是否有下一页
}
