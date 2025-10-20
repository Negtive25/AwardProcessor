package org.com.code.certificateProcessor.pojo.enums;

import lombok.Getter;

/**
 * 学习内容类型枚举
 */
@Getter
public enum ContentType {
    STANDARD_AWARD("standard_award");

    private final String type;
    ContentType(String type) {
        this.type = type;
    }
} 