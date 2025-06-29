package ject.petfit.domain.schedule.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ScheduleException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public ScheduleException(ScheduleErrorCode scheduleErrorCode) {
        super(scheduleErrorCode.getMessage());
        this.httpStatus = scheduleErrorCode.getHttpStatus();
        this.code = scheduleErrorCode.getCode();
    }
}
