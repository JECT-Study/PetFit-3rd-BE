package ject.petfit.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum AuthUserErrorCode {
    PROFILE_REQUEST_ERROR(HttpStatus.BAD_REQUEST,"프로필 요청에 오류가 발생했습니다"),
    OAUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 서버와의 통신에 실패했습니다"),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일을 찾을 수 없습니다"),
    PROFILE_INFORMATION_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "카카오 프로필 정보가 부족합니다."),
    AUTH_EMAIL_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일을 가진 사용자가 없습니다."),;

    private final HttpStatus httpStatus;
    private final String message;
}
