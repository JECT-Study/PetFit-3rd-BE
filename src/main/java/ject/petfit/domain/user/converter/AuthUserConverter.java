package ject.petfit.domain.user.converter;

import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;

public class AuthUserConverter {
    public static AuthUser toUser(Long kakaoUUID, String email, String nickName, String password) {
        return AuthUser.builder()
                .kakaoUUID(kakaoUUID)
                .email(email)
                .nickname(nickName)
                .encodedPassword(password)
                .build();
    }

    public static AuthUserResponseDTO.JoinResultDTO toJoinResultDTO(AuthUser authUser, String accessToken) {
        return AuthUserResponseDTO.JoinResultDTO.builder()
                .userId(authUser.getId())
                .kakaoUUID(authUser.getKakaoUUID())
                .email(authUser.getEmail())
                .nickname(authUser.getNickname())
                .accessToken(accessToken)
                .role(authUser.getMember().getRole())
                .build();
    }


}
