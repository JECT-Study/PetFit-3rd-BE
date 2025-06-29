package ject.petfit.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-414", "사용자를 찾을 수 없습니다"),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
