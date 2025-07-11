package ject.petfit.global.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-441", "유효하지 않은 토큰입니다"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN-451", "토큰이 존재하지 않습니다"),
    AUTH_EXPIRE_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-461", "만료된 토큰입니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN-471", "리프레시 토큰이 존재하지 않습니다"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN-481", "유효하지 않은 리프레시 토큰입니다"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-491","만료된 리프레시 토큰입니다"),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
