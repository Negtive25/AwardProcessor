package org.com.code.certificateProcessor.pojo.modelInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//这个类专门用来映射Elasticsearch中包含向量的文档。
@Getter
@Setter
@NoArgsConstructor
//它告诉JSON解析器，如果JSON 中有类里不存在的字段，请忽略不要报错
@JsonIgnoreProperties(ignoreUnknown = true)
public class VectorDoc {
    private float[] average_vector;
}
