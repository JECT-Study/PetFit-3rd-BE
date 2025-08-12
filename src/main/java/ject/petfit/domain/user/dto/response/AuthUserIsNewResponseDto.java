package ject.petfit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserIsNewResponseDto {
    private Long memberId;
    private boolean isNewUser;
}
