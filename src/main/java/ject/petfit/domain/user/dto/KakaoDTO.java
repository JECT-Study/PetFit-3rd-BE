package ject.petfit.domain.user.dto;

import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import lombok.Getter;

public class KakaoDTO {

    @Getter
    public static class OAuthToken {
        private String accessToken;
        private String token_type;
        private RefreshToken refreshToken;
        private int expiresIn;
        private String scope;
        private int refresh_token_expires_in;
    }

    @Getter
    public static class KakaoProfile {
        private Long id;
        private String connectedAt;
        private Properties properties;
        private KakaoAccount kakaoAccount;

        @Getter
        public class Properties {
            private String nickname;
        }

        @Getter
        public class KakaoAccount {
            private String email;
            private Boolean isEmailVerified;
            private Boolean hasEmail;
            private Boolean profile_nickname_needs_agreement;
            private Boolean email_needs_agreement;
            private Boolean isEmailValid;
        }
    }
}
