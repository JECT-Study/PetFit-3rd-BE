package ject.petfit.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserSimpleResponseDto {
    private Long memberId;
    private String name;
    private String nickname;
    private String email;
}
