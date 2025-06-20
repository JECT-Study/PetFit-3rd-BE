package ject.petfit.global.jwt.refreshtoken;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

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
    public AuthUser validateAndRotateToken(String oldRawRefreshToken) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(oldRawRefreshToken);

        if (!passwordEncoder.matches(oldRawRefreshToken, oldToken.getToken())) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }
        // 토큰 로테이션: 기존 토큰 삭제
        AuthUser user = oldToken.getAuthUser();
        refreshTokenRepository.delete(oldToken);
        user.addRefreshToken(null);

        return user;
    }

    private String hashToken(String rawToken) {
        return passwordEncoder.encode(rawToken);
    }
}
