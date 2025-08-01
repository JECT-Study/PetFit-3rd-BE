package ject.petfit.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum AuthUserErrorCode {
    PROFILE_REQUEST_ERROR(HttpStatus.BAD_REQUEST,"USER-400", "프로필 요청에 오류가 발생했습니다"),
    OAUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER-500","OAuth 서버와의 통신에 실패했습니다"),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER-430","이메일을 찾을 수 없습니다"),
    PROFILE_INFORMATION_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "USER-440","카카오 프로필 정보가 부족합니다."),
    AUTH_EMAIL_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-434","해당 이메일을 가진 사용자가 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-414", "사용자를 찾을 수 없습니다"),
    NOT_AN_AUTHENTICATED_USER(HttpStatus.NOT_FOUND, "USER-424", "인증된 사용자가 아닙니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-444", "해당 리프레쉬 토큰을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
