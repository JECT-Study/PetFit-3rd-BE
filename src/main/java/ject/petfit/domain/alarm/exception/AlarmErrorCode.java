package ject.petfit.domain.alarm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlarmErrorCode {
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALARM-404", "일정을 찾을 수 없습니다."),
    ALARM_EDIT_TIME_INVALID(HttpStatus.BAD_REQUEST, "ALARM-400", "과거 날짜의 일정은 수정할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
