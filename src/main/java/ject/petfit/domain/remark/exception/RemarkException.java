package ject.petfit.domain.remark.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RemarkException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public RemarkException(RemarkErrorCode remarkErrorCode) {
        super(remarkErrorCode.getMessage());
        this.httpStatus = remarkErrorCode.getHttpStatus();
        this.code = remarkErrorCode.getCode();
        this.message = remarkErrorCode.getMessage();
    }
}
