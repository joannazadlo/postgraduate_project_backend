package io.github.joannazadlo.recipedash.exception.mealDb;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MealDbServiceUnavailableException extends BaseException {

    public MealDbServiceUnavailableException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message, cause);
    }
}
