package org.com.code.certificateProcessor.pojo.enums;

public enum AwardSubmissionStatus {
    AI_PROCESSING,
    AI_APPROVED,
    AI_REJECTED,
    MANUAL_APPROVED,
    MANUAL_REJECTED,

    /**
     * AI 处理过程中发生异常（如模型、ES、DB连接失败）
     * 经过3次重试后，自动转为人工审核。
     */
    ERROR_NEED_TO_MANUAL_REVIEW;
}
