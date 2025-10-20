package org.com.code.certificateProcessor.exeption;

public class getAdaptiveCompressedUrl extends RuntimeException {
    public getAdaptiveCompressedUrl(String message) {
        super(message);
    }
    public getAdaptiveCompressedUrl(String message, Throwable cause) {
        super(message, cause);
    }
    public String getMessage() {
        return super.getMessage();
    }
}
