package ject.petfit.global.jwt.refreshtoken;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RefreshToken createOrUpdateRefreshToken(AuthUser authUser, String rawToken, long validitySeconds) {
        String hashedToken = hashToken(rawToken); // SHA-256 등으로 해시화
        Instant expires_at = Instant.now().plusSeconds(validitySeconds);

        return refreshTokenRepository.findByAuthUser(authUser)
                .map(existingToken -> {
                    existingToken.updateToken(hashedToken, expires_at);
                    return existingToken;
                })
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken(authUser, hashedToken, expires_at);
                    authUser.addRefreshToken(newToken);
                    return refreshTokenRepository.save(newToken);
                });
    }

    @Transactional
    public AuthUser validateAndRotateToken(String oldRawRefreshToken) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(oldRawRefreshToken)
                .orElseThrow(() -> new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!passwordEncoder.matches(oldRawRefreshToken, oldToken.getToken())) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }

        AuthUser user = oldToken.getAuthUser();
        user.removeRefreshToken();

        return user;
    }

    private String hashToken(String rawToken) {
        return passwordEncoder.encode(rawToken);
    }
}
