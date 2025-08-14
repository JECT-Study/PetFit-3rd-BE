package ject.petfit.domain.slot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SlotErrorCode {
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SLOT-404", "해당 슬롯을 찾을 수 없습니다."),
    SLOT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SLOT-CATEGORY-404", "해당 슬롯 카테고리를 찾을 수 없습니다."),
    SLOT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SLOT-409", "해당 반려동물에 대한 슬롯이 이미 존재합니다."),
    SLOT_NOT_ACTIVATED(HttpStatus.BAD_REQUEST, "SLOT-400", "활성화된 슬롯 옵션이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
