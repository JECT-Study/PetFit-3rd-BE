package ject.petfit.domain.member.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage());
        this.httpStatus = memberErrorCode.getHttpStatus();
        this.code = memberErrorCode.getCode();
    }
}