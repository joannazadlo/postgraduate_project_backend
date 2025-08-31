package io.github.joannazadlo.recipedash.exception.tasty;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TastyServiceUnavailableException extends BaseException {

    public TastyServiceUnavailableException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message, cause);
    }
}
