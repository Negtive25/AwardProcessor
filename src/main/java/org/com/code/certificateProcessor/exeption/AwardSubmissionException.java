package org.com.code.certificateProcessor.exeption;

public class AwardSubmissionException extends RuntimeException {
    public AwardSubmissionException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
    public AwardSubmissionException(String message) {
        super(message);
    }
}
