package io.github.joannazadlo.recipedash.constants;

public final class TastyApiConstants {

    public static final String BASE_URL = "https://tasty.p.rapidapi.com";
    public static final String RECIPES_LIST = BASE_URL + "/recipes/list";
    public static final String RECIPE_INFO = BASE_URL + "/recipes/get-more-info?id=";
    public static final String RAPIDAPI_KEY_HEADER = "X-RapidAPI-Key";
    public static final String RAPIDAPI_HOST_HEADER = "X-RapidAPI-Host";
    public static final String RAPIDAPI_HOST_VALUE = "tasty.p.rapidapi.com";

    private TastyApiConstants() {}
}
