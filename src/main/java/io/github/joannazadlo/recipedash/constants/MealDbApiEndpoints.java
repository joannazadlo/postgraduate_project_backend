package io.github.joannazadlo.recipedash.constants;

public final class MealDbApiEndpoints {

    public static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1";
    public static final String FILTER_BY_INGREDIENT = BASE_URL + "/filter.php?i=";
    public static final String FILTER_BY_CUISINE = BASE_URL + "/filter.php?a=";
    public static final String FILTER_BY_CATEGORY = BASE_URL + "/filter.php?c=";
    public static final String LOOKUP_BY_ID = BASE_URL + "/lookup.php?i=";

    private MealDbApiEndpoints() {}
}
