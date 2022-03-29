package com.mysite.sbb.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}