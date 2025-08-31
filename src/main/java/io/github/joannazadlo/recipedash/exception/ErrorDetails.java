package io.github.joannazadlo.recipedash.exception;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDetails implements Serializable {
    private int status;
    private String error;
    private final String errorMessage;
    private LocalDateTime timestamp;
}
