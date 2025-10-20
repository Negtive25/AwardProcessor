package org.com.code.certificateProcessor.pojo.enums;

import lombok.Getter;

@Getter
public enum Auth {
    ADMIN("Admin"),
    STUDENT("Student");

    private final String type;
    private Auth(String type) {
        this.type = type;
    }
}
