package ject.petfit.domain.slot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SlotException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public SlotException(SlotErrorCode slotErrorCode) {
        super(slotErrorCode.getMessage());
        this.httpStatus = slotErrorCode.getHttpStatus();
        this.code = slotErrorCode.getCode();
    }
}
