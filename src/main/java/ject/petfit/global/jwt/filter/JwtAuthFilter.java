package ject.petfit.global.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. 요청 헤더에서 JWT 추출
        String token = jwtUtil.resolveAccessToken(request);

        try {
            if (token != null && jwtUtil.isTokenValid(token)) {
                // 2. 토큰에서 이메일 추출
                String email = jwtUtil.getEmail(token);
                // 3. DB에서 사용자 조회 (CustomUserDetailsService 사용)
                UserDetails userDetails = authUserService.loadAuthUserByEmail(email);
                // 4. SecurityContext에 인증 정보 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("jwt-exception", "토큰 만료");
        } catch (TokenException | IllegalArgumentException e) {
            request.setAttribute("jwt-exception", "유효하지 않은 토큰");
        }

        filterChain.doFilter(request, response);
    }
}