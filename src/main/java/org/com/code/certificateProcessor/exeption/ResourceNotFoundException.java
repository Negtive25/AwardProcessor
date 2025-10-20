package org.com.code.certificateProcessor.exeption;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
