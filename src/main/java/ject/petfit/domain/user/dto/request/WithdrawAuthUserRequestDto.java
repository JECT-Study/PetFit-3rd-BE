package ject.petfit.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawAuthUserRequestDto {

    @NotBlank
    private String refreshToken;
}
