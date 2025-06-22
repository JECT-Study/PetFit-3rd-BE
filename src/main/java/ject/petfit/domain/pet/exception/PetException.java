package ject.petfit.domain.pet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PetException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public PetException(PetErrorCode petErrorCode) {
        super(petErrorCode.getMessage());
        this.httpStatus = petErrorCode.getHttpStatus();
        this.code = petErrorCode.getCode();
    }
}
