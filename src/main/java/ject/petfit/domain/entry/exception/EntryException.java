package ject.petfit.domain.entry.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntryException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public EntryException(EntryErrorCode entryErrorCode) {
        super(entryErrorCode.getMessage());
        this.httpStatus = entryErrorCode.getHttpStatus();
        this.code = entryErrorCode.getCode();
        this.message = entryErrorCode.getMessage();
    }
}
