package org.com.code.certificateProcessor.exeption;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
        System.out.println("ResourceNotFoundException: " + message);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
}
