package ject.petfit.domain.aireport.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AiReportException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public AiReportException(AiReportErrorCode aiReportErrorCode) {
        super(aiReportErrorCode.getMessage());
        this.httpStatus = aiReportErrorCode.getHttpStatus();
        this.code = aiReportErrorCode.getCode();
    }
}
