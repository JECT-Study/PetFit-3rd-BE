package ject.petfit.domain.user.converter;

import ject.petfit.domain.user.entity.AuthUser;

public class AuthUserConverter {
    public static AuthUser toUser(Long kakaoUUID, String email, String nickName, String password) {
        return AuthUser.builder()
                .kakaoUUID(kakaoUUID)
                .email(email)
                .nickname(nickName)
                .encodedPassword(password)
                .build();
    }
}
