package ject.petfit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 커스텀 처리할 오류
    DEV_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다(커스텀 예외 처리)"),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다"),
    PROFILE_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "프로필 요청에 오류가 발생했습니다"),
    OAUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 서버와의 통신에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}