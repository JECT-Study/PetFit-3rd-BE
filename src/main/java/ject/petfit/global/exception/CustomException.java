package ject.petfit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}