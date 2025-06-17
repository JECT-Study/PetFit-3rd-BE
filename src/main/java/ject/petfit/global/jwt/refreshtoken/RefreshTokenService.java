package ject.petfit.global.jwt.refreshtoken;

import jakarta.transaction.Transactional;
import java.time.Instant;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RefreshToken createOrUpdateRefreshToken(AuthUser authUser, String rawToken, long validitySeconds) {
        String hashedToken = hashToken(rawToken); // SHA-256 등으로 해시화
        Instant expirationTime = Instant.now().plusSeconds(validitySeconds);

        return refreshTokenRepository.findByAuthUser(authUser)
                .map(existingToken -> {
                    existingToken.updateToken(hashedToken, expirationTime);
                    return existingToken;
                })
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken(authUser, hashedToken, expirationTime);
                    authUser.addRefreshToken(newToken);
                    return refreshTokenRepository.save(newToken);
                });
    }

    @Transactional
    public void validateAndRotateToken(AuthUser authUser, String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!passwordEncoder.matches(rawToken, refreshToken.getHashedRefreshToken())) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }
        // 토큰 로테이션: 기존 토큰 삭제 후 새 토큰 발급
        refreshTokenRepository.delete(refreshToken);
        authUser.addRefreshToken(null);
    }

    private String hashToken(String rawToken) {
        return passwordEncoder.encode(rawToken);
    }
}
