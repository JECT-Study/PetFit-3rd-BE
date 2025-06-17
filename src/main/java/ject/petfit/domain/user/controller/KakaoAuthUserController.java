package ject.petfit.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.global.jwt.util.JwtUtil;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoAuthUserController {

    private final AuthUserService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;


    // 최초 소셜 로그인/회원가입
    @GetMapping("/auth/kakao/login")
    public ResponseEntity<AuthUserResponseDTO.JoinResultDTO> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        AuthUser user = authService.oAuthLogin(accessCode, httpServletResponse);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, UUID.randomUUID().toString(), refreshTokenValiditySeconds);
        user.addRefreshToken(refreshToken);

        AuthUserResponseDTO.JoinResultDTO dto = AuthUserConverter.toJoinResultDTO(user, accessToken);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/auth/protected")
    public ResponseEntity<String> protectedResource(Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자명
        return ResponseEntity.ok("인증된 사용자: " + username);
    }


    // 리프레시 코드 재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request) {
        // 토큰 유효성 검사 및 삭제
        AuthUser authUser = refreshTokenService.validateAndRotateToken(request.getRefreshToken());
        // 새로운 access 토큰 발급
        String newAccessToken = jwtUtil.createAccessToken(authUser.getEmail(), authUser.getMember().getRole().name());
        // 새로운 refresh 토큰 발급
        String newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, UUID.randomUUID().toString(),
                                    refreshTokenValiditySeconds).getHashedRefreshToken();
        return ResponseEntity.ok(new TokenResponseDTO(newAccessToken, newRefreshToken));
    }
}
