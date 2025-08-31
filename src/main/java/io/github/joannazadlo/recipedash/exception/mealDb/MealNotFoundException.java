package io.github.joannazadlo.recipedash.exception.mealDb;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MealNotFoundException extends BaseException {

    public MealNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
