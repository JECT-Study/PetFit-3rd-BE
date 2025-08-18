package ject.petfit.global.jwt.refreshtoken.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    public AuthUser validateRefreshToken(String receivedHashedToken, String email) {
        // 쿠키에 해시된 토큰이 전달된다고 가정
        RefreshToken storedToken = refreshTokenRepository.findByToken(receivedHashedToken)
                .orElseThrow(() -> new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        AuthUser user = storedToken.getAuthUser();
        if (!user.getEmail().equals(email)) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID);
        }

        return user;
    }

    private String hashToken(String rawToken) {
        return passwordEncoder.encode(rawToken);
    }

    public Optional<RefreshToken> findTokenByCookie(String hashedTokenFromCookie) {
        // 쿠키에 해시 토큰이 옴
        return refreshTokenRepository.findByToken(hashedTokenFromCookie);
    }
}
