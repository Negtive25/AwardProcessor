package org.com.code.certificateProcessor.pojo.enums;

import lombok.Getter;

@Getter
public enum Auth {
    SUPER_ADMIN("Super_Admin"),
    ADMIN("Admin"),
    STUDENT("Student");

    private final String name;
    private Auth(String name) {
        this.name = name;
    }
}
