package io.github.joannazadlo.recipedash.exception.image;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidFileFormatException extends BaseException {

    public InvalidFileFormatException(String message) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }
}
