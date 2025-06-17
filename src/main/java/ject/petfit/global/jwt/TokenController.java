package ject.petfit.global.jwt;

import java.util.UUID;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.dto.TokenResponseDTO;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

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
