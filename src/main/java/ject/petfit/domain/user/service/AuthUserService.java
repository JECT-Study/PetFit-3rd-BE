package ject.petfit.domain.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.user.common.util.KakaoUtil;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.KakaoDto;
import ject.petfit.domain.user.dto.response.AuthUserIsNewResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.UUID;

@Service
@Slf4j
public class AuthUserService {

    private final KakaoUtil kakaoUtil;
    private final JwtUtil jwtUtil;
    private final AuthUserRepository authUserRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    public AuthUserService(WebClient.Builder webClientBuilder,
                           KakaoUtil kakaoUtil,
                           JwtUtil jwtUtil,
                           AuthUserRepository authUserRepository,
                           @Lazy RefreshTokenRepository refreshTokenRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           MemberRepository memberRepository,
                           @Lazy RefreshTokenService refreshTokenService) {
        this.kakaoUtil = kakaoUtil;
        this.jwtUtil = jwtUtil;
        this.authUserRepository = authUserRepository;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.webClient = webClientBuilder.build();
    }


    @Transactional
    public AuthUser oAuthLogin(String accessCode) {
        try {
            // 1. 카카오 토큰 요청
            KakaoDto.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
            if (oAuthToken == null) {
                throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
            }
            // 2. 카카오 프로필 요청
            KakaoDto.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
            if (kakaoProfile == null || kakaoProfile.getKakao_account() == null) {
                throw new AuthUserException(AuthUserErrorCode.PROFILE_REQUEST_ERROR);
            }
            // 3. 이메일 확인
            String email = kakaoProfile.getKakao_account().getEmail();
            if (email == null || email.isEmpty()) {
                throw new AuthUserException(AuthUserErrorCode.EMAIL_NOT_FOUND);
            }
            // 4. 사용자 조회 또는 생성
            try {
                AuthUser user = findOrCreateUser(email, kakaoProfile);
                return user;
            } catch (Exception e) {
                throw e;
            }
        } catch (AuthUserException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthUserException(AuthUserErrorCode.OAUTH_SERVER_ERROR);
        }
    }

    private AuthUser findOrCreateUser(String email, KakaoDto.KakaoProfile kakaoProfile) {
        return authUserRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.changeIsNewUser(false);
                    return authUserRepository.save(existingUser);
                })
                .orElseGet(() -> createNewUser(kakaoProfile));
    }


    private AuthUser createNewUser(KakaoDto.KakaoProfile kakaoProfile) {
        if (kakaoProfile == null || kakaoProfile.getKakao_account() == null) {
            throw new AuthUserException(AuthUserErrorCode.PROFILE_INFORMATION_NOT_SUPPORTED);
        }

        Member newMember = Member.builder()
                .nickname(kakaoProfile.getKakao_account().getProfile().getNickname())
                .role(Role.USER)
                .build();
        memberRepository.save(newMember);

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        AuthUser newUser = AuthUserConverter.toUser(
                kakaoProfile.getId(),
                kakaoProfile.getKakao_account().getEmail(),
                kakaoProfile.getKakao_account().getProfile().getNickname(),
                kakaoProfile.getKakao_account().getProfile().getNickname(),
                encodedPassword,
                true
        );
        newUser.addMember(newMember);
        authUserRepository.save(newUser);
        newMember.addAuthUser(newUser);

        return newUser;
    }

    public AuthUser loadAuthUserByMemberId(Long memberId) {
        return authUserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.AUTH_EMAIL_USER_NOT_FOUND));
    }

    public AuthUser getAuthUser(Long authUserId) {
        return authUserRepository.findById(authUserId)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.USER_NOT_FOUND));
    }

    @Async
    public void logoutAsync(String accessToken) {
        CompletableFuture.runAsync(() -> {
            try {
                // 카카오 API 호출
                webClient.post()
                        .uri("https://kapi.kakao.com/v1/user/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();

                // DB 정리 (선택적)
                if (accessToken != null) {
                    Long memberId = jwtUtil.getMemberId(accessToken);
                    AuthUser user = loadAuthUserByMemberId(memberId);
                    RefreshToken refreshToken = user.getRefreshToken();
                    refreshTokenRepository.delete(refreshToken);
                }

                log.info("Background logout completed for token: {}", accessToken);
            } catch (Exception e) {
                log.error("Background logout failed: {}", e.getMessage());
            }
        });
    }

    @Async
    public void withdrawAsync(
            Long userId, String refreshToken, String kakaoUserId, String adminKey) {
        CompletableFuture.runAsync(() -> {
            try {
                // 카카오 unlink
                unlinkUserByAdminKey(kakaoUserId, adminKey);

                withdraw(userId, refreshToken);

            } catch (Exception e) {
                log.error("Background withdraw failed: {}", e.getMessage());
            }
        });
    }

    @Transactional
    public void withdraw(Long userId, String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
        authUserRepository.deleteById(userId);
    }

    public void unlinkUserByAdminKey(String kakaoUserId, String adminKey) {
        WebClient.create()
                .post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("target_id_type", "user_id")
                        .with("target_id", kakaoUserId))
                .retrieve()
                .toBodilessEntity()
                .then()
                .block();
    }

    public AuthUserIsNewResponseDto isNewUserFromMemberId(Long memberId) {
        AuthUser authUser = authUserRepository.findByMemberIdWithPets(memberId)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.USER_NOT_FOUND));

        List<Pet> existingPets = authUser.getMember().getPets();
        if (existingPets.isEmpty()) {
            return new AuthUserIsNewResponseDto(authUser.getMember().getId(), true);
        } else {
            return new AuthUserIsNewResponseDto(authUser.getMember().getId(), false);
        }
    }
}
