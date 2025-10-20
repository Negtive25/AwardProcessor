package org.com.code.certificateProcessor.pojo.enums;

public enum AwardSubmissionStatus {
    AI_PROCESSING,
    AI_APPROVED,
    AI_REJECTED,
    MANUAL_APPROVED,
    MANUAL_REJECTED,

    /**
     * 新增状态：AI 处理过程中发生异常（如模型、ES、DB连接失败）
     * 经过3次重试后，自动转为人工审核。
     */
    AI_ERROR_MANUAL_REVIEW;
}
