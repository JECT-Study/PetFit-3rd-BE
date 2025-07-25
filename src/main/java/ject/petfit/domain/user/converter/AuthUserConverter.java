package ject.petfit.domain.user.converter;

import ject.petfit.domain.user.dto.response.AuthUserResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;

public class AuthUserConverter {
    public static AuthUser toUser(Long kakaoUUID, String email, String name, String nickName, String password, boolean isNewUser) {
        return AuthUser.builder()
                .kakaoUUID(kakaoUUID)
                .email(email)
                .name(name)
                .nickname(nickName)
                .encodedPassword(password)
                .isNewUser(isNewUser)
                .build();
    }

    public static AuthUserResponseDto.JoinResultDTO toJoinResultDTO(AuthUser authUser, String accessToken, RefreshToken refreshToken) {
        return AuthUserResponseDto.JoinResultDTO.builder()
                .userId(authUser.getId())
                .kakaoUUID(authUser.getKakaoUUID())
                .email(authUser.getEmail())
                .nickname(authUser.getNickname())
                .accessToken(accessToken)
                .role(authUser.getMember().getRole())
                .isNewUser(authUser.getIsNewUser())
                .refreshToken(refreshToken.getToken())
                .build();
    }


}
