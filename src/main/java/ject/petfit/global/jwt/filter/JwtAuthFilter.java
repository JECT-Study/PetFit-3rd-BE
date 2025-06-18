package ject.petfit.global.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@RequiredArgsConstructor
@Component
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
        // 요청 URL 로깅
        log.info("Request URL: {}", request.getRequestURL());

        // 인증이 필요 없는 엔드포인트는 토큰 체크 건너뛰기
        if (request.getRequestURI().startsWith("/auth/") ||
                request.getRequestURI().equals("/favicon.ico") ||
                request.getRequestURI().startsWith("/.well-known/") ||
                request.getRequestURI().equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        for (String headerName : Collections.list(request.getHeaderNames())) {
            if (headerName.equals("Authorization")) {
                log.info("Header {}: {}", headerName, request.getHeader(headerName));
            }
        }

        try {
            // 1. 요청 헤더에서 JWT 추출
            String token = jwtUtil.resolveAccessToken(request);
            log.info("Resolved token: {}", token);

            if (token != null && jwtUtil.isTokenValid(token)) {
                // 2. 토큰에서 이메일 추출
                String email = jwtUtil.getEmail(token);
                log.info("Extracted email: {}", email);

                // 3. DB에서 사용자 조회
                UserDetails userDetails = authUserService.loadAuthUserByEmail(email);
                // 4. SecurityContext에 인증 정보 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set in SecurityContext");
            }
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            request.setAttribute("jwt-exception", "토큰 만료");
        }
//        catch (TokenException | IllegalArgumentException e) {
//            log.error("Token validation failed", e);
//            request.setAttribute("jwt-exception", "유효하지 않은 토큰");
//        }

        filterChain.doFilter(request, response);
    }
}