package io.github.joannazadlo.recipedash.exception.tasty;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TastyRecipeNotFoundException extends BaseException {

    public TastyRecipeNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
