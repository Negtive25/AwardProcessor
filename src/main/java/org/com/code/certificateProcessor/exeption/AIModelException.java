package org.com.code.certificateProcessor.exeption;

public class AIModelException extends RuntimeException {
    public AIModelException(String message) {
        super(message);
        System.out.println("AIModelException: " + message);
    }
    public AIModelException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
}
