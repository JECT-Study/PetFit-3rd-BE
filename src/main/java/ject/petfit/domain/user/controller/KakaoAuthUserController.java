package ject.petfit.domain.user.controller;


import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.request.AuthUserRequestDTO;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.InvalidGrantException;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.global.jwt.util.JwtUtil;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class KakaoAuthUserController {

    private final AuthUserService authUserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;


    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody AuthUserRequestDTO request) {
        try {
            Mono<TokenResponse> tokenResponse = authUserService.exchangeCodeForToken(request.getAccess_code());
            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidGrantException e) {
            // 인가 코드 재사용 또는 만료 시
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "invalid_grant", "message", "인가 코드가 만료되었거나 이미 사용되었습니다. 다시 로그인하세요."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "server_error", "message", "서버 오류가 발생했습니다."));
        }
    }

    // 최초 소셜 로그인/회원가입
    @GetMapping("/auth/kakao/login")
    public ResponseEntity<AuthUserResponseDTO.JoinResultDTO> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        AuthUser user = authUserService.oAuthLogin(accessCode, httpServletResponse);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        httpServletResponse.setContentType("application/json");

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

}
