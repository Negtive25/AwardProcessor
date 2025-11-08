package org.com.code.certificateProcessor.exeption;

public class OSSException extends RuntimeException {
    public OSSException(String message) {
        super(message);
        System.out.println("OSSException: " + message);
    }
    public OSSException(String message,Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
}
