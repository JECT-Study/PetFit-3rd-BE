package ject.petfit.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawAuthUserRequest {

    @NotBlank
    private String refreshToken;
}
