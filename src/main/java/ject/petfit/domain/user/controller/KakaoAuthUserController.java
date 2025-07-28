package ject.petfit.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.response.AuthUserIsNewResponseDto;
import ject.petfit.domain.user.dto.response.AuthUserResponseDto;
import ject.petfit.domain.user.dto.response.AuthUserSimpleResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDto;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.CookieUtils;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthUserController {

    private final AuthUserService authUserService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    @Value("${spring.kakao.auth.admin}")
    private String adminKey;

    @Value("${spring.front}")
    private String frontDomain;


    // 소셜 로그인/회원가입 -> 쿠키
    @GetMapping("/kakao/login")
    public void kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        // Cookie
//        httpServletResponse.addCookie(CookieUtils.addCookie("access_token", accessToken));
//        httpServletResponse.addCookie(CookieUtils.addCookie("refresh_token", refreshToken.getToken()));

//        httpServletResponse.sendRedirect(frontDomain);
        // 쿠키 설정 (헤더가 아닌 HttpServletResponse의 addCookie 사용)
//        CookieUtils.addCookie("access_token", accessToken, httpServletResponse);
//        CookieUtils.addCookie("refresh_token", refreshToken.getToken(), httpServletResponse);

        // 리다이렉트
        httpServletResponse.sendRedirect(frontDomain + "token?access_token=" + accessToken + "&refresh_token=" + refreshToken.getToken());
//        AuthUserTokenResponseDto tokenResponseDto = new AuthUserTokenResponseDto(accessToken, refreshToken.getToken());
    }

    // 소셜 로그인/회원가입 -> DEV
    @GetMapping("/kakao/login/dev")
    public ResponseEntity<ApiResponse<AuthUserResponseDto.JoinResultDTO>> kakaoLoginDev(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        AuthUserResponseDto.JoinResultDTO joinResultDTO = AuthUserConverter.toJoinResultDTO(user, accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(joinResultDTO)
        );
    }

    // 서비스만 로그아웃 -> 쿠키 삭제
    // UX 고려하여 카카오 계정과의 unlink 처리는 X
    @PostMapping("/kakao/logout")
    public ResponseEntity<ApiResponse<?>> logout(
         @CookieValue(name = "access_token", required = false) String accessToken,
         @CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        authUserService.logout(accessToken);
        // 리프레시 토큰 무효화
        refreshTokenService.findTokenByPlain(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
        // 클라이언트 정리 지시
        response.addCookie(CookieUtils.deleteCookieByName("access_token"));
        response.addCookie(CookieUtils.deleteCookieByName("refresh_token"));
        response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(null)
        );
    }

    // 서비스만 로그아웃 -> Dev
    @PostMapping("/kakao/logout/dev")
    public ResponseEntity<ApiResponse<?>> logoutDev(
            @RequestBody RefreshTokenRequestDto request, HttpServletResponse response) {
        // 리프레시 토큰 무효화
        refreshTokenService.findTokenByPlain(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);

        // 프론트엔드에 토큰 삭제 지시
        response.setHeader("X-Clear-Tokens", "true");
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(null)
        );
    }

    // 회원 탈퇴 (JWT 기반 Refresh Token 삭제 후 카카오 계정과의 unlink 처리)
    // UX 고려하여 회원 탈퇴 시 카카오 계정과의 unlink 처리
    @PostMapping("/kakao/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletRequest request,
                                         Authentication authentication) {
        // JWT 필터에서 이미 검증된 정보 사용
        String email = authentication.getName();
        AuthUser user = authUserService.loadAuthUserByEmail(email);

        // Refresh Token 추가 검증
        RefreshToken refreshToken = refreshTokenService.findTokenByPlain(request.getHeader("Authorization"))
                .orElseThrow(() -> new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!refreshToken.getAuthUser().getId().equals(user.getId())) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }
        // 카카오 계정과의 unlink 처리
        authUserService.unlinkUserByAdminKey(user.getKakaoUUID().toString(), adminKey);

        // 회원 탈퇴 처리
        authUserService.withdraw(user.getId(), request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.success(null)
        );
    }

    @GetMapping("/accesscookie")
    public ResponseEntity<ApiResponse<AuthUserSimpleResponseDto>> refreshTokenToInfoCookie(
            HttpServletRequest request, HttpServletResponse response
    ) {
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");
        AuthUserSimpleResponseDto memberInfo = authUserService.getMemberInfoFromRefreshTokenCookie(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(null)
        );
    }

    @PostMapping("/token/cookie")
    public ResponseEntity<ApiResponse<AuthUserIsNewResponseDto>> returnTokenCookie(
            @RequestParam String accessToken, @RequestParam String refreshToken) {
        AuthUserIsNewResponseDto isNewResponseDto = authUserService.isNewUserFromRefreshToken(refreshToken);

        // SameSite=None이 적용된 쿠키 생성
        ResponseCookie accessCookie = CookieUtils.createTokenCookie("access_token", accessToken);
        ResponseCookie refreshCookie = CookieUtils.createTokenCookie("refresh_token", refreshToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + accessToken)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(
                        ApiResponse.success(isNewResponseDto)
                );
    }

//    @GetMapping("/verify")
//    public ResponseEntity<ApiResponse<Boolean>> verifyCookies(HttpServletRequest request) {
//
//        // Set-Cookie 헤더에서 토큰 추출
//        String accessToken = refreshTokenService.extractTokenFromSetCookie(request, "access_token");
//        String refreshToken = refreshTokenService.extractTokenFromSetCookie(request, "refresh_token");
//
//        boolean isAuthenticated = (accessToken != null && !accessToken.isEmpty())
//                && (refreshToken != null && !refreshToken.isEmpty());
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ApiResponse.success(isAuthenticated)
//        );
//    }

}
