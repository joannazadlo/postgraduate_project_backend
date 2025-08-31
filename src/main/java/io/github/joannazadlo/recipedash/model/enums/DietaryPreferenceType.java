package io.github.joannazadlo.recipedash.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DietaryPreferenceType {

    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten-Free"),
    DAIRY_FREE("Dairy-Free");
    private final String displayName;

    DietaryPreferenceType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }


    @JsonCreator
    public static DietaryPreferenceType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        for (DietaryPreferenceType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown dietary preference type: " + displayName);
    }

    public static DietaryPreferenceType fromString(String name) {
        try {
            return DietaryPreferenceType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean contains(String name) {
        for (DietaryPreferenceType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
