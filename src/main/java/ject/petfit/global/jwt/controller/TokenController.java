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
        @CookieValue(name = "access_token", required = false) String expiredAccessToken
    ) {
        // 쿠키가 없는 경우 처리
        if (expiredAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.fail("TOKEN-401", "토큰이 없습니다.")
            );
        }
        
        try {
            Long memberId = jwtUtil.getMemberId(expiredAccessToken);
            AuthUser authUser = authUserService.loadAuthUserByEmail(memberId);

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(
                    authUser.getEmail(), authUser.getMember().getRole().name(), authUser.getMember().getId());

            // 새로운 리프레시 토큰 생성 및 저장
            RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser,
                UUID.randomUUID().toString(), refreshTokenValiditySeconds);

            // SameSite=None이 적용된 쿠키 생성
            ResponseCookie accessCookie = CookieUtils.createTokenCookie("access_token", newAccessToken);
            ResponseCookie refreshCookie = CookieUtils.createTokenCookie("refresh_token", newRefreshToken.getToken());

            return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.fail("TOKEN-401", "토큰이 유효하지 않습니다.")
        );
    }
}
}
