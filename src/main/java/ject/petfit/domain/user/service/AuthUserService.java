package ject.petfit.domain.user.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.common.util.KakaoUtil;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.KakaoDto;
import ject.petfit.domain.user.dto.response.AuthUserSimpleResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@Slf4j
public class AuthUserService {

    private final KakaoUtil kakaoUtil;
    private final AuthUserRepository authUserRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    public AuthUserService(WebClient.Builder webClientBuilder,
                           KakaoUtil kakaoUtil,
                           AuthUserRepository authUserRepository,
                           @Lazy RefreshTokenRepository refreshTokenRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           MemberRepository memberRepository) {
        this.kakaoUtil = kakaoUtil;
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

    public AuthUser loadAuthUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.AUTH_EMAIL_USER_NOT_FOUND));
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
                .header("Authorization", "KakaoAK " + adminKey)
                .bodyValue("target_id_type=user_id&target_id=" + kakaoUserId)
                .retrieve()
                .toBodilessEntity()
                .then()
                .block();

    }

    public AuthUserSimpleResponseDto getMemberInfoFromRefreshTokenCookie(String refreshToken) {
        AuthUser authUser = refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenEntity -> refreshTokenEntity.getAuthUser())
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.REFRESH_TOKEN_NOT_FOUND));

        return AuthUserSimpleResponseDto.builder()
                .memberId(authUser.getMember().getId())
                .name(authUser.getName())
                .nickname(authUser.getNickname())
                .email(authUser.getEmail())
                .build();
    }
}
