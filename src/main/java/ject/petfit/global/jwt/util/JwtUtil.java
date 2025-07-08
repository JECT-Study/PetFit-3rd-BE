package ject.petfit.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {

    private final String issuer;
    private final SecretKey secretKey;
    private final long accessTokenValidityMilliseconds;

    public JwtUtil(
            @Value("${spring.jwt.issuer}") final String issuer,
            @Value("${spring.jwt.secret}") final String secretKey,
            @Value("${spring.jwt.access-token-time}") final long accessTokenValidityMilliseconds) {
        this.issuer = issuer;
        // JWT Secret Key를 안전하게 생성
        this.secretKey = createSecretKey(secretKey);
        this.accessTokenValidityMilliseconds = accessTokenValidityMilliseconds;
    }

    private SecretKey createSecretKey(String secretKeyString) {
        try {
            // 환경변수에서 받은 secret을 사용하여 SecretKey 생성
            byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
            
            // 키 길이가 256비트(32바이트) 미만이면 안전한 키 생성
            if (keyBytes.length < 32) {
                log.warn("JWT secret key is too short ({} bits). Generating a secure key.", keyBytes.length * 8);
                return Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
            
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.warn("Failed to create JWT secret key from environment variable. Generating a secure key.", e);
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    // 토큰이 없을 때 null을 반환하도록 수정
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        // Authorization 헤더가 없는 경우 / 비어있는 경우 / Bearer로 시작하지 않는 경우 / 토큰이 없는 경우
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = authorization.split(" ");
        if (!authorization.startsWith("Bearer ") || parts.length != 2 || parts[1].trim().isEmpty()) {
            return null;
        }
        return parts[1].trim();
    }

    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenValidityMilliseconds);
    }

    private String createToken(String email, String role, long validityMilliseconds) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiration = now.plusSeconds(validityMilliseconds / 1000);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityMilliseconds))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return getClaims(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException
                 | ExpiredJwtException | TokenException e) {
            return false;
        }
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

    }
}