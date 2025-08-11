package ject.petfit.global.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 인증이 필요 없는 엔드포인트는 토큰 체크 건너뛰기
        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.info("=== JWT Filter Start ===");
        log.info("Request Method: {}, URI: {}", method, uri);
        
        // URI 체크 로직 디버깅
        boolean shouldSkip = shouldSkipJwtCheck(uri);
        log.info("URI: '{}', shouldSkip: {}", uri, shouldSkip);
        
        // 인증이 필요 없는 엔드포인트 체크를 가장 먼저 수행
        if (shouldSkip) {
            log.info("✅ Skipping JWT check for URI: {}", uri);
            log.info("=== JWT Filter End (Skipped) ===");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("🔒 JWT check required for URI: {}", uri);

        // Authorization 헤더 로깅
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            log.info("Authorization header found: {}", authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
        } else {
            log.info("No Authorization header found");
        }

        try {
            // 1. 요청 헤더에서 JWT 추출
            String token = jwtUtil.resolveAccessToken(request);

            if (token != null) {
                log.info("Token extracted successfully");
                if (jwtUtil.isTokenValid(token)) {
                    log.info("Token is valid");
                    // 2. 토큰에서 이메일 추출
                    String email = jwtUtil.getEmail(token);
                    log.info("Email from token: {}", email);

                    // 3. DB에서 사용자 조회
                    UserDetails userDetails = authUserService.loadAuthUserByEmail(email);
                    // 4. SecurityContext에 인증 정보 저장
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set successfully for user: {}", email);
                } else {
                    log.warn("Token is invalid");
                    throw new TokenException(TokenErrorCode.AUTH_INVALID_TOKEN);
                }
            } else {
                log.info("No token found, proceeding without authentication");
            }
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            request.setAttribute("jwt-exception", "토큰 만료");
        } catch (TokenException e) {
            log.error("Invalid token", e);
            request.setAttribute("jwt-exception", "유효하지 않은 토큰");
        } catch (Exception e) {
            log.error("Unexpected JWT processing error", e);
        }

        log.info("=== JWT Filter End ===");
        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipJwtCheck(String uri) {
        boolean result =
                uri.startsWith("/api/auth") ||
                uri.equals("/") ||
               uri.equals("/error") ||
               uri.startsWith("/swagger-ui") ||
               uri.startsWith("/v3") ||
               uri.startsWith("/api-docs") ||
               uri.startsWith("/swagger-resources") ||
               uri.startsWith("/health") ||
               uri.startsWith("/api/pets") ||
               uri.startsWith("/api/members") ||
               uri.startsWith("/dev") ||
                uri.startsWith("/api/routines") ||
                uri.startsWith("/api/remarks") ||
                uri.startsWith("/api/schedules") ||
                uri.startsWith("/api/slots") ||
                uri.startsWith("/api/entries") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/favicon.png") ||
                uri.startsWith("/static/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/locales/") ||
                uri.startsWith("/public/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/resources/") ||
                uri.startsWith("/token");

        log.info("Final result: {}", result);
        return result;
    }
}
