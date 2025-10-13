package ject.petfit.global.jwt.controller;


import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.dto.ReissueTokenResponseDto;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.CookieUtils;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final AuthUserService authUserService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    // Refresh Token 재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<Void>> reIssue(
        @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        // 1. 쿠키에 refresh_token이 없으면 401 반환
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("TOKEN-401", "리프레시 토큰이 없습니다."));
        }

        try {
            // 2. Refresh Token 검증 (DB나 Redis에 저장된 토큰과 비교)
            RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

            // 3. AuthUser 정보 로드
            AuthUser authUser = authUserService.getAuthUser(storedToken.getAuthUser().getId());

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(
                authUser.getEmail(), authUser.getMember().getRole().name(), authUser.getMember().getId());

            // 새로운 리프레시 토큰 생성 및 저장
            RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser,
                UUID.randomUUID().toString(), refreshTokenValiditySeconds);
            // 6. 쿠키 생성
            ResponseCookie accessCookie = CookieUtils.createTokenCookie("access_token", newAccessToken);
            ResponseCookie refreshCookie = CookieUtils.createTokenCookie("refresh_token", newRefreshToken.getToken());

            // 7. 응답 반환
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("TOKEN-401", "토큰이 유효하지 않습니다."));
        }
    }
}

