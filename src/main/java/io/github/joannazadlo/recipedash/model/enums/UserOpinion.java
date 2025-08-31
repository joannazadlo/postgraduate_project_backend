package io.github.joannazadlo.recipedash.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserOpinion {

    LIKE,
    DISLIKE,
    NEUTRAL;

    @JsonCreator
    public static UserOpinion fromString(String value) {
        return value == null ? null : UserOpinion.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
