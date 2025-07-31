package ject.petfit.domain.pet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PetErrorCode{
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET-404", "해당 반려동물을 찾을 수 없습니다."),
    PET_NOT_BELONG_TO_MEMBER(HttpStatus.BAD_REQUEST, "PET-400", "해당 반려동물은 회원의 소유가 아닙니다."),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
