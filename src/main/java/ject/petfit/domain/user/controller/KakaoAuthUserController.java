package ject.petfit.domain.user.controller;


import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.request.AuthUserRequestDTO;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.InvalidGrantException;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.global.jwt.util.CookieUtils;
import ject.petfit.global.jwt.util.JwtUtil;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoAuthUserController {

    private final AuthUserService authUserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    // 소셜 로그인/회원가입 -> 쿠키
    @GetMapping("/auth/kakao/login")
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

    @GetMapping("/auth/protected")
    public ResponseEntity<Map<String, String>> protectedResource(
            Authentication authentication,
            HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization").replace("Bearer ", "");
        String username = authentication.getName();

        Map<String, String> response = Map.of(
                "name", "인증된 사용자: " + username,
                "accessToken", accessToken
        );
        return ResponseEntity.ok(response);
    }

}
