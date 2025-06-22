package ject.petfit.domain.user.dto.response;

import ject.petfit.domain.member.entity.Role;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthUserResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class JoinResultDTO {
        private Long userId;
        private Long kakaoUUID;
        private String email;
        private String nickname;
        private String accessToken;
        private Role role;
        private boolean isNewUser;
        private RefreshToken refreshToken;
    }
}
