package org.com.code.certificateProcessor.exeption;

public class StudentException extends RuntimeException {
    public StudentException(String message) {
        super(message);
    }
    public StudentException(String message, Throwable cause) {
        super(message, cause);
    }
    public String getMessage() {
        return super.getMessage();
    }
}
