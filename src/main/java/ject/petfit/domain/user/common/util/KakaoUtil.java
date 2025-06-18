package ject.petfit.domain.user.common.util;

import ject.petfit.domain.user.dto.KakaoDTO;
import ject.petfit.domain.user.dto.KakaoDTO.OAuthToken;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KakaoUtil {
    @Value("${spring.kakao.auth.client}")
    private String client;
    @Value("${spring.kakao.auth.redirect}")
    private String redirect;
    private final WebClient webClient;

    public KakaoUtil(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public KakaoDTO.OAuthToken requestToken(String accessCode) {
        KakaoDTO.OAuthToken token = null;
        try {
            token = webClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .body(BodyInserters.fromFormData(params(accessCode)))
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR)))
                    .bodyToMono(KakaoDTO.OAuthToken.class)
                    .block();

            // 토큰을 받은 후 로깅
            if (token != null) {
                new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
            }
            return token;
        } catch (Exception e) {
            new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
        }
        return token;
    }


    public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken) {
        try {
            if (oAuthToken == null || oAuthToken.getAccess_token() == null) {
                throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
            }

            KakaoDTO.KakaoProfile profile = webClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + oAuthToken.getAccess_token())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR)))
                    .bodyToMono(KakaoDTO.KakaoProfile.class)
                    .doOnNext(p -> log.info("Kakao profile received: id={}, nickname={}",
                            p.getId(),
                            p.getKakao_account().getProfile().getNickname()))
                    .block();

            if (profile == null) {
                throw new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR);
            }

            return profile;
        } catch (Exception e) {
            log.error("Failed to get Kakao profile: {}", e.getMessage());
            throw new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR);
        }
    }

    public MultiValueMap<String, String> params(String accessCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_uri", redirect);
        params.add("code", accessCode);
        return params;
    }
}
