package io.github.joannazadlo.recipedash.exception.user;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
