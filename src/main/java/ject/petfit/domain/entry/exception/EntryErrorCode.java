package ject.petfit.domain.entry.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EntryErrorCode {
    ENTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "ENTRY-404", "기록을 찾을 수 없습니다."),
    ENTRY_ALREADY_EXISTS(HttpStatus.CONFLICT, "ENTRY-409", "이미 존재하는 기록 입니다."),
    INVALID_ENTRY_REQUEST(HttpStatus.BAD_REQUEST, "ENTRY-400", "유효하지 않은 기록 요청입니다."),
    ENTRY_NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "ENTRY-501", "getOrCreateEntry() 메서드의 로직이 잘못 작동하고 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
