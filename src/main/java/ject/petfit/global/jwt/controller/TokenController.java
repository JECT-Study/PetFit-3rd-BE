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
            HttpServletRequest request
//            , HttpServletResponse httpServletResponse
    ) {
        // 토큰 유효성 검사 및 삭제
        AuthUser authUser = refreshTokenService.validateAndRotateToken(request.getHeader("Authorization"));
        // 새로운 access 토큰 발급
        String newAccessToken = jwtUtil.createAccessToken(authUser.getEmail(), authUser.getMember().getRole().name());
        // 새로운 refresh 토큰 발급
        String newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, UUID.randomUUID().toString(),
                refreshTokenValiditySeconds).getToken();
        //쿠키 처리
//        httpServletResponse.addCookie(CookieUtils.addCookie("access_token", newAccessToken));
//        httpServletResponse.addCookie(CookieUtils.addCookie("refresh_token", newRefreshToken));

        AuthUserTokenResponseDto tokenResponseDto = new AuthUserTokenResponseDto(newAccessToken, newRefreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(tokenResponseDto)
        );
    }
}
