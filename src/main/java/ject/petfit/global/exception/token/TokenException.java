package ject.petfit.global.exception.token;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public TokenException(TokenErrorCode tokenErrorCode) {
        super(tokenErrorCode.getMessage());
        this.httpStatus = tokenErrorCode.getHttpStatus();
        this.message = tokenErrorCode.getMessage();
    }
}
