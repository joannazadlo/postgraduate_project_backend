package io.github.joannazadlo.recipedash.exception.recipe;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RecipeNotFoundException extends BaseException {

    public RecipeNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
