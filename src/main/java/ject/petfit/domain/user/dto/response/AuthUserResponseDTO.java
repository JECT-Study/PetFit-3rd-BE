package ject.petfit.domain.user.dto.response;

import java.time.LocalDateTime;
import ject.petfit.domain.member.entity.Role;
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
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserPreviewDTO {
        private Long userId;
        private Long kakaoUUID;
        private String email;
        private String nickname;
        private String jwtAccessToken;
        private Role role;
    }
}
