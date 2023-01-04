package org.swyg.greensumer.domain.constant;

import lombok.Getter;

public enum UserRole {
    USER("유저"), SELLER("판매자"), ADMIN("관리자");

    @Getter private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
