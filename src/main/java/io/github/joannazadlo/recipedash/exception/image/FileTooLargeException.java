package io.github.joannazadlo.recipedash.exception.image;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileTooLargeException extends BaseException {

    public FileTooLargeException(String message) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, message);
    }
}
