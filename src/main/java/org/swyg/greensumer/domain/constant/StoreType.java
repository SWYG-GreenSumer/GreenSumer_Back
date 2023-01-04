package org.swyg.greensumer.domain.constant;

import lombok.Getter;

public enum StoreType {
    HOUSEHOLD_GOODS("생활용품"),
    FOOD("식품");

    @Getter private final String description;

    StoreType(String description) { this.description = description; }
}
