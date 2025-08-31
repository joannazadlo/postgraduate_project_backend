package io.github.joannazadlo.recipedash.utils;

import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MealDbUtilsTest {

    @Test
    void hasUnsupportedPreferences_shouldReturnTrue_WhenUnsupportedPreferencesPresent() {
        List<DietaryPreferenceType> preferences = List.of(
                DietaryPreferenceType.GLUTEN_FREE,
                DietaryPreferenceType.VEGETARIAN
        );

        assertTrue(MealDbUtils.hasUnsupportedPreferences(preferences));
    }

    @Test
    void hasUnsupportedPreferences_shouldReturnFalse_WhenOnlyVeganAndVegetarian() {
        List<DietaryPreferenceType> preferences = List.of(
                DietaryPreferenceType.VEGAN,
                DietaryPreferenceType.VEGETARIAN
        );

        assertFalse(MealDbUtils.hasUnsupportedPreferences(preferences));
    }

    @Test
    void hasUnsupportedPreferences_shouldReturnFalse_WhenNoDietaryPreferencesProvided() {
        assertFalse(MealDbUtils.hasUnsupportedPreferences(null));
    }
}
