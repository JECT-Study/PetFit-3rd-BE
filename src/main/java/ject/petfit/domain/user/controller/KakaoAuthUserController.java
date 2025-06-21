package ject.petfit.domain.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.request.WithdrawAuthUserRequest;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenRepository;
import ject.petfit.global.jwt.util.CookieUtils;
import ject.petfit.global.jwt.util.JwtUtil;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoAuthUserController {

    private final AuthUserService authUserService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    @Value("${spring.kakao.auth.client}")
    private String clientId;

    @Value("${spring.kakao.auth.admin}")
    private String adminKey;

    @Value("${spring.kakao.auth.logout.redirect}")
    private String redirectUri;

    // 소셜 로그인/회원가입 -> 쿠키
    @GetMapping("/auth/kakao/login/cookie")
    public void kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        // Cookie
        httpServletResponse.addCookie(CookieUtils.addCookie("access_token", accessToken));
        httpServletResponse.addCookie(CookieUtils.addCookie("refresh_token", refreshToken.getToken()));
        
        httpServletResponse.sendRedirect("http://localhost:3000/home");
    }

    // 소셜 로그인/회원가입 -> DEV
    @GetMapping("/auth/kakao/login")
    public ResponseEntity<AuthUserResponseDTO.JoinResultDTO> kakaoLoginDev(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        AuthUserResponseDTO.JoinResultDTO dto = AuthUserConverter.toJoinResultDTO(user, accessToken, refreshToken);

        httpServletResponse.sendRedirect("http://localhost:3000/home"); //?

        return ResponseEntity.ok(dto);
    }

    // 서비스만 로그아웃
    @PostMapping("/auth/kakao/logout")
    public ResponseEntity<?> logout(
            @RequestBody RefreshTokenRequestDTO request, HttpServletResponse response) {
        // 리프레시 토큰 무효화
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
        // 클라이언트 정리 지시
        response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        return ResponseEntity.ok().build();
    }

    // 서비스 + 카카오 연동 로그아웃
    @PostMapping("/auth/kakao/logout/kakaoAll")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String kakaoToken = extractToken(request, "Kakao-Access-Token");

        // JWT Refresh Token 무효화 (Access Token 블랙리스트 등록 X)
        refreshTokenRepository.findByToken(extractRefreshTokenFromCookie(request))
                .ifPresent(refreshTokenRepository::delete);
        // 카카오 토큰 무효화
        revokeKakaoToken(kakaoToken);

        response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        // 완전 로그아웃 URL 생성
        String logoutUrl = buildKakaoLogoutUrl();
        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, logoutUrl)
                .body(Map.of("logoutUrl", logoutUrl));
    }

    // 회원 탈퇴 (JWT 기반 Refresh Token 삭제 후 카카오 계정과의 unlink 처리)
    // UX 고려하여 회원 탈퇴 시 카카오 계정과의 unlink 처리
    @DeleteMapping("/auth/kakao/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody WithdrawAuthUserRequest request,
                                         Authentication authentication) {
        // JWT 필터에서 이미 검증된 정보 사용
        String email = authentication.getName();
        AuthUser user = authUserService.loadAuthUserByEmail(email);

        // Refresh Token 추가 검증
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!refreshToken.getAuthUser().getId().equals(user.getId())) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }
        // 카카오 계정과의 unlink 처리
        unlinkUserByAdminKey(user.getKakaoUUID().toString());

        // 회원 탈퇴 처리
        authUserService.withdraw(user.getId(), request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    public Mono<Void> unlinkUserByAdminKey(String kakaoUserId) {
        return WebClient.create()
                .post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .header("Authorization", "KakaoAK " + adminKey)
                .bodyValue("target_id_type=user_id&target_id=" + kakaoUserId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }

    private String extractToken(HttpServletRequest request, String headerName) {
        String header = request.getHeader(headerName);
        return header != null ? header.replace("Bearer ", "") : null;
    }

    // 토큰 기반 카카오 계정과의 unlink
    private Mono<Void> revokeKakaoToken(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        return WebClient.create()
                .post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    private String buildKakaoLogoutUrl() {
        return "https://kauth.kakao.com/oauth/logout" +
                "?client_id=" + clientId +
                "&logout_redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
    }

}
