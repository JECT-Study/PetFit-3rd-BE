package ject.petfit.domain.user.common.util;

import ject.petfit.domain.user.dto.KakaoDto;
import ject.petfit.domain.user.dto.KakaoDto.OAuthToken;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public OAuthToken requestToken(String accessCode) {
        try {
            OAuthToken token = webClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .body(BodyInserters.fromFormData(params(accessCode)))
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR)))
                    .bodyToMono(OAuthToken.class)
                    .block();

            // 토큰을 받은 후 로깅
            if (token != null) {
                log.info("Kakao OAuth token received successfully");
                return token;
            } else {
                log.error("Failed to receive Kakao OAuth token");
                throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error requesting Kakao OAuth token", e);
            throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
        }
    }


    public KakaoDto.KakaoProfile requestProfile(OAuthToken oAuthToken) {
        try {
            if (oAuthToken == null || oAuthToken.getAccess_token() == null) {
                throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
            }

            KakaoDto.KakaoProfile profile = webClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + oAuthToken.getAccess_token())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR)))
                    .bodyToMono(KakaoDto.KakaoProfile.class)
                    .doOnNext(p -> log.info("Kakao profile received: id={}, nickname={}",
                            p.getId(),
                            p.getKakao_account().getProfile().getNickname()))
                    .block();

            if (profile == null) {
                throw new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR);
            }

            return profile;
        } catch (Exception e) {
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
