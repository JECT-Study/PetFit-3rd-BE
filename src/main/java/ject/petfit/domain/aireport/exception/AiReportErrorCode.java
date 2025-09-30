package ject.petfit.domain.aireport.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiReportErrorCode {
    AI_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "AI-404", "AI 리포트를 찾을 수 없습니다."),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
