package io.github.joannazadlo.recipedash.exception.user;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotBlockYourselfException extends BaseException {

    public CannotBlockYourselfException() {
        super(HttpStatus.BAD_REQUEST, "You cannot block yourself");
    }
}
