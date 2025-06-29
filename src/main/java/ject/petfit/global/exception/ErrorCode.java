package ject.petfit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 커스텀 처리할 오류
    DEV_NOT_FOUND(HttpStatus.NOT_FOUND, "EXAMPLE-404", "사용자를 찾을 수 없습니다(커스텀 예외 처리)"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "COMMON-400", "입력 날짜 형식이 맞지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
