package ject.petfit.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.dto.response.AuthUserIsNewResponseDto;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.jwt.dto.AccessTokenRequestDto;
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

    @Value("${app.front.domain}")
    private String frontDomain;
    @Value("${app.front.local}")
    private String frontLocal;


    // 소셜 로그인/회원가입 -> 쿠키
    @GetMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 (운영용)", description = "카카오 로그인 후 토큰을 쿠키에 저장합니다.")
    public ResponseEntity<ApiResponse<Void>> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(
                user.getEmail(), user.getMember().getRole().toString(), user.getMember().getId());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        // SameSite=None이 적용된 쿠키 생성
        ResponseCookie accessCookie = CookieUtils.createTokenCookie("access_token", accessToken);
        ResponseCookie refreshCookie = CookieUtils.createTokenCookie("refresh_token", refreshToken.getToken());

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(ApiResponse.success(null));
    }

    // 소셜 로그인/회원가입 -> DEV
    @GetMapping("/kakao/login/dev")
    @Operation( summary = "카카오 로그인 (개발용)", description = "개발 환경에서 카카오 로그인 후 토큰을 쿠키에 저장합니다.")
    public ResponseEntity<ApiResponse<Void>> kakaoLoginDev(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) throws IOException {
        AuthUser user = authUserService.oAuthLogin(accessCode);

        String accessToken = jwtUtil.createAccessToken(
                user.getEmail(), user.getMember().getRole().toString(), user.getMember().getId());
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        // SameSite=None이 적용된 쿠키 생성
        ResponseCookie accessCookie = CookieUtils.createTokenCookie("access_token", accessToken);
        ResponseCookie refreshCookie = CookieUtils.createTokenCookie("refresh_token", refreshToken.getToken());

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(ApiResponse.success(null));
    }

    // 서비스만 로그아웃 -> 쿠키 삭제
    // UX 고려하여 카카오 계정과의 unlink 처리는 X
    @PostMapping("/kakao/logout")
    @Operation(summary = "카카오 로그아웃 (운영용)", description = "카카오 로그아웃 후 쿠키를 삭제합니다.")
    public ResponseEntity<ApiResponse<?>> logout(
         @CookieValue(name = "access_token", required = false) String accessToken) {

        // 즉시 쿠키 삭제 응답
        ResponseCookie accessCookie = CookieUtils.deleteTokenCookie("access_token");
        ResponseCookie refreshCookie = CookieUtils.deleteTokenCookie("refresh_token");

        // 백그라운드에서 카카오 API 호출 및 DB 정리
        authUserService.logoutAsync(accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"")
                .body(ApiResponse.success(null));
    }

    // 서비스만 로그아웃 -> Dev
    @PostMapping("/kakao/logout/dev")
    @Operation(summary = "카카오 로그아웃 (개발용)", description = "개발 환경에서 카카오 로그아웃 후 쿠키를 삭제합니다.")
    public ResponseEntity<ApiResponse<?>> logoutDev(
            @RequestBody AccessTokenRequestDto request, HttpServletResponse response) {

        // 백그라운드에서 카카오 API 호출 및 DB 정리
        authUserService.logoutAsync(request.getAccessToken());

        // 프론트엔드에 토큰 삭제 지시
        response.setHeader("X-Clear-Tokens", "true");
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(null)
        );
    }

    // 회원 탈퇴 (JWT 기반 Refresh Token 삭제 후 카카오 계정과의 unlink 처리)
    // UX 고려하여 회원 탈퇴 시 카카오 계정과의 unlink 처리
    // 수정된 코드 (권장)
    @PostMapping("/kakao/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시 카카오 계정과의 unlink 처리 및 토큰 삭제")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @CookieValue(name = "access_token") String accessToken
    ) {
        Long memberId = jwtUtil.getMemberId(accessToken);
        AuthUser user = authUserService.loadAuthUserByMemberId(memberId);
        RefreshToken refreshTokenEntity = user.getRefreshToken();

        ResponseCookie accessCookie = CookieUtils.deleteTokenCookie("access_token");
        ResponseCookie refreshCookie = CookieUtils.deleteTokenCookie("refresh_token");

        authUserService.withdrawAsync(
            user.getId(),
            refreshTokenEntity.getToken(),
            user.getKakaoUUID().toString(),
            adminKey
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"")
                .body(ApiResponse.success(null));
    }

    @PostMapping("/me")
    @Operation(summary = "액세스 토큰으로 유저 정보 확인",
        description = "액세스 토큰으로 유저 정보를 확인하고, 신규 유저인지 여부를 반환합니다.")
    public ResponseEntity<ApiResponse<AuthUserIsNewResponseDto>> returnTokenCookie(
        @CookieValue("access_token") String accessToken
    ) {
        Long memberId = jwtUtil.getMemberId(accessToken);

        AuthUserIsNewResponseDto isNewResponseDto = authUserService.isNewUserFromMemberId(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.success(isNewResponseDto)
        );
    }

//    @GetMapping("/verify")
//    public ResponseEntity<ApiResponse<Boolean>> verifyCookies(
//            @CookieValue("access_token") String accessToken,
//            @CookieValue("refresh_token") String refreshToken) {
//
//        boolean isAuthenticated = (accessToken != null && !accessToken.isEmpty())
//                && (refreshToken != null && !refreshToken.isEmpty());
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ApiResponse.success(isAuthenticated)
//        );
//    }

}
