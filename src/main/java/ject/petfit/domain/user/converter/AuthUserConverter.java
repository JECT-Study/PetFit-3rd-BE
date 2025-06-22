package ject.petfit.domain.user.converter;

import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;

public class AuthUserConverter {
    public static AuthUser toUser(Long kakaoUUID, String email, String nickName, String password, boolean isNewUser) {
        return AuthUser.builder()
                .kakaoUUID(kakaoUUID)
                .email(email)
                .nickname(nickName)
                .encodedPassword(password)
                .isNewUser(isNewUser)
                .build();
    }

    public static AuthUserResponseDTO.JoinResultDTO toJoinResultDTO(AuthUser authUser, String accessToken, RefreshToken refreshToken) {
        return AuthUserResponseDTO.JoinResultDTO.builder()
                .userId(authUser.getId())
                .kakaoUUID(authUser.getKakaoUUID())
                .email(authUser.getEmail())
                .nickname(authUser.getNickname())
                .accessToken(accessToken)
                .role(authUser.getMember().getRole())
                .isNewUser(authUser.getIsNewUser())
                .refreshToken(refreshToken)
                .build();
    }


}
