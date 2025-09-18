package ject.petfit.domain.alarm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlarmException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public AlarmException(AlarmErrorCode alarmErrorCode) {
        super(alarmErrorCode.getMessage());
        this.httpStatus = alarmErrorCode.getHttpStatus();
        this.code = alarmErrorCode.getCode();
    }
}
