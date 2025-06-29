package ject.petfit.domain.slot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SlotErrorCode {
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SLOT-404", "해당 슬롯을 찾을 수 없습니다."),
    TARGET_AMOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "TARGET-404", "해당 목표량을 찾을 수 없습니다."),
    SLOT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SLOT-409", "해당 반려동물에 대한 슬롯이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
