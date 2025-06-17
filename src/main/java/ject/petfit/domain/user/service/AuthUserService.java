package ject.petfit.domain.user.service;

import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.UUID;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.common.util.KakaoUtil;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.KakaoDTO;
import ject.petfit.domain.user.dto.KakaoDTO.OAuthToken;
import ject.petfit.domain.user.exception.InvalidGrantErrorCode;
import ject.petfit.domain.user.exception.InvalidGrantException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthUserService {

    private final KakaoUtil kakaoUtil;
    private final AuthUserRepository authUserRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    public AuthUserService(WebClient.Builder webClientBuilder,
                           KakaoUtil kakaoUtil,
                           AuthUserRepository authUserRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           MemberRepository memberRepository) {
        this.kakaoUtil = kakaoUtil;
        this.authUserRepository = authUserRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.webClient = webClientBuilder.build();
    }


    @Transactional
    public AuthUser oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        // custom error handling for missing kakaoProfile 처리 필
        String email = kakaoProfile.getKakao_account().getEmail();

        AuthUser user = authUserRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(accessCode, kakaoProfile));

        return user;
    }

    private AuthUser createNewUser(String accessCode, KakaoDTO.KakaoProfile kakaoProfile) {
        // error handling for missing profile information
        if (kakaoProfile == null || kakaoProfile.getKakao_account() == null) {
            throw new IllegalArgumentException("카카오 프로필 정보가 부족합니다.");
        }

        Member newMember = Member.builder()
                .nickname(kakaoProfile.getProperties().getNickname())
                .role(Role.USER)
                .build();
        memberRepository.save(newMember);

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        AuthUser newUser = AuthUserConverter.toUser(
                getKakaoUUID(accessCode),
                kakaoProfile.getKakao_account().getEmail(),
                kakaoProfile.getProperties().getNickname(),
                encodedPassword
        );
        newUser.addMember(newMember);

        return authUserRepository.save(newUser);
    }

    public Mono<TokenResponse> exchangeCodeForToken(String code) {
        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData(kakaoUtil.params(code)))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }


    public Long getKakaoUUID(String accessCode) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode); // oAuthToken(access 토큰) 요청
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        return kakaoProfile.getId();
    }

    public AuthUser loadAuthUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
    }
}
