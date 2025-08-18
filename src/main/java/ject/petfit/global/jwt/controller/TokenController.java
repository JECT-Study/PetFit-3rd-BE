package ject.petfit.global.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.dto.response.AuthUserTokenResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDto;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.CookieUtils;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    // Refresh Token 재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<AuthUserTokenResponseDto>> refresh(
        @CookieValue(name = "access_token", required = false) String expiredAccessToken,
        @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        // 쿠키가 없는 경우 처리
        if (expiredAccessToken == null || refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("토큰이 없습니다.")
            );
        }
        
        try {
            String email = jwtUtil.getEmailFromExpiredToken(expiredAccessToken);
            AuthUser authUser = refreshTokenService.validateRefreshToken(refreshToken, email);
            
            String newAccessToken = jwtUtil.createAccessToken(authUser.getEmail(), authUser.getMember().getRole().name());
            String newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, UUID.randomUUID().toString(),
                    refreshTokenValiditySeconds).getToken();
    
            AuthUserTokenResponseDto tokenResponseDto = new AuthUserTokenResponseDto(newAccessToken, newRefreshToken);
    
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success(tokenResponseDto)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("토큰이 유효하지 않습니다.")
        );
    }
}
}
