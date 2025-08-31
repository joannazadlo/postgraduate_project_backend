package io.github.joannazadlo.recipedash.exception.userIngredient;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserIngredientNotFoundException extends BaseException {

    public UserIngredientNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
