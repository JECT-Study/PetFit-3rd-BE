package ject.petfit.domain.user.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import ject.petfit.domain.user.dto.KakaoDTO;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.global.exception.CustomException;
import ject.petfit.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData(params(accessCode)))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR)))
                .bodyToMono(KakaoDTO.OAuthToken.class)
                .doOnNext(token -> log.info("oAuthToken : {}", token.getAccessToken()))
                .block();
    }

    public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + oAuthToken.getAccessToken())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR)))
                .bodyToMono(KakaoDTO.KakaoProfile.class)
                .block();
    }

    private MultiValueMap<String, String> params(String accessCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_uri", redirect);
        params.add("code", accessCode);
        return params;
    }
}
