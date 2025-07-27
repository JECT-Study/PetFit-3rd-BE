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
    public AuthUser validateAndRotateToken(String oldRawRefreshToken) {
        RefreshToken oldToken = refreshTokenRepository.findAll().stream()
                .filter(t -> passwordEncoder.matches(oldRawRefreshToken, t.getToken()))
                .findFirst()
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

    public Optional<RefreshToken> findTokenByPlain(String plainToken) {
        return refreshTokenRepository.findAll().stream()
                .filter(token -> passwordEncoder.matches(plainToken, token.getToken()))
                .findFirst();
    }

    public String extractTokenFromSetCookie(HttpServletRequest request, String cookieName) {
        String setCookieHeader = request.getHeader("Set-Cookie");
        if (setCookieHeader == null) {
            return null;
        }

        // Set-Cookie 헤더에서 특정 쿠키 값 추출
        String[] cookies = setCookieHeader.split(";");
        for (String cookie : cookies) {
            if (cookie.trim().startsWith(cookieName + "=")) {
                return cookie.substring(cookieName.length() + 1);
            }
        }

        return null;
    }
}
