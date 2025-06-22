package ject.petfit.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidGrantException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public InvalidGrantException(InvalidGrantErrorCode invalidGrantErrorCode) {
        super(invalidGrantErrorCode.getMessage());
        this.httpStatus = invalidGrantErrorCode.getHttpStatus();
        this.message = invalidGrantErrorCode.getMessage();
        this.code = invalidGrantErrorCode.getCode();
    }
}
