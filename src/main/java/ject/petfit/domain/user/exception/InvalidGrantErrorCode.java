package ject.petfit.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InvalidGrantErrorCode {
    INVALID_GRANT_ERROR_CODE(HttpStatus.UNAUTHORIZED, "인증 코드가 만료되었거나 이미 사용되었습니다"),;

    private final HttpStatus httpStatus;
    private final String message;
}
