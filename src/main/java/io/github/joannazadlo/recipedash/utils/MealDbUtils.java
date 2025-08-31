package io.github.joannazadlo.recipedash.utils;

import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;

import java.util.List;

public class MealDbUtils {

    private MealDbUtils() {}

    public static boolean hasUnsupportedPreferences(List<DietaryPreferenceType> preferences) {
        if (preferences == null) return false;
        return preferences.stream()
                .anyMatch(p -> !(p == DietaryPreferenceType.VEGAN
                        || p == DietaryPreferenceType.VEGETARIAN));
    }
}
