package org.com.code.certificateProcessor.ElasticSearch;

public class ESConst {
    public static final int Vector_DIM = 768; // 向量维度
    // 索引名常量
    public static final String STANDARD_AWARD = "standard_award";

    // 内部字段枚举
    public enum StandardAwardField {
        StandardAwardId("standardAwardId"),
        AwardName("awardName"),
        IsActive("isActive"),
        NameVector("nameVector");

        private final String fieldName;
        StandardAwardField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }
}
