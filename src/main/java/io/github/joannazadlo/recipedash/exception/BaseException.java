package io.github.joannazadlo.recipedash.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor(force = true)
public class BaseException extends RuntimeException {

    @NonNull
    private HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public BaseException(HttpStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public BaseException(HttpStatus errorStatus, String message, Throwable cause) {
        super(message, cause);
        this.errorStatus = errorStatus;
    }

    public ErrorDetails toErrorDetails() {
        return ErrorDetails.builder()
                .status(errorStatus.value())
                .error(errorStatus.name())
                .errorMessage(getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }
}
