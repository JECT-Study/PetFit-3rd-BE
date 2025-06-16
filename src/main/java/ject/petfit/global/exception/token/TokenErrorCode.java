package ject.petfit.global.exception.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다"),
    AUTH_EXPIRE_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
