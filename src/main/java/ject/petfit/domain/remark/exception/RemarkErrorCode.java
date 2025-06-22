package ject.petfit.domain.remark.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RemarkErrorCode {
    REMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "REMARK-404", "특이사항을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
