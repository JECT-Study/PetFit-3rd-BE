package ject.petfit.domain.routine.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoutineException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
     private final String message;

    public RoutineException(RoutineErrorCode routineErrorCode) {
        super(routineErrorCode.getMessage());
        this.httpStatus = routineErrorCode.getHttpStatus();
        this.code = routineErrorCode.getCode();
        this.message = routineErrorCode.getMessage();
    }
}
