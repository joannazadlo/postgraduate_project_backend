package io.github.joannazadlo.recipedash.exception.image;

import io.github.joannazadlo.recipedash.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ImageUploadException extends BaseException {

    public ImageUploadException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
