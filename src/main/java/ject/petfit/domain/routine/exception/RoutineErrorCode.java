package ject.petfit.domain.routine.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoutineErrorCode {
    ROUTINE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE-404", "해당하는 루틴 카테고리를 찾을 수 없습니다."),
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE-404", "해당하는 루틴을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
