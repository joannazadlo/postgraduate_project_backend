package io.github.joannazadlo.recipedash.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CuisineType {

    CARIBBEAN("Caribbean"),
    ASIAN("Asian"),
    EUROPEAN("European"),
    NORTH_AMERICAN("North American"),
    AFRICAN("African"),
    JEWISH("Jewish"),
    CENTRAL_SOUTH_AMERICAN("Central South American"),
    MIDDLE_EASTERN("Middle Eastern"),
    OTHER("Other");
    private final String displayName;

    CuisineType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static CuisineType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        for (CuisineType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown cuisine type: " + displayName);
    }
}
