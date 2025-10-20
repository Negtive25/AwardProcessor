package org.com.code.certificateProcessor.exeption;

public class AdminException extends RuntimeException {
    public AdminException(String message) {
        super(message);
    }
    public AdminException(String message, Throwable cause) {
        super(message, cause);
    }
    public String getMessage() {
        return super.getMessage();
    }
}
