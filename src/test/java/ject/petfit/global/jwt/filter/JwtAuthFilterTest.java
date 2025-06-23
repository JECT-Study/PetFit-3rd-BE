package ject.petfit.global.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthUserService authUserService;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private AuthUser authUser;
    private Member member;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        member = Member.builder()
                .nickname("testUser")
                .role(Role.USER)
                .build();

        authUser = AuthUser.builder()
                .kakaoUUID(12345L)
                .email("test@example.com")
                .nickname("testUser")
                .encodedPassword("encodedPassword")
                .isNewUser(true)
                .build();
        authUser.addMember(member);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공")
    void doFilterInternal_유효한토큰_인증성공() throws Exception {
        // given
        String validToken = "valid.jwt.token";
        String email = "test@example.com";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singletonList("Authorization")));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.resolveAccessToken(request)).thenReturn(validToken);
        when(jwtUtil.isTokenValid(validToken)).thenReturn(true);
        when(jwtUtil.getEmail(validToken)).thenReturn(email);
        when(authUserService.loadAuthUserByEmail(email)).thenReturn(authUser);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(authUserService).loadAuthUserByEmail(email);
        
        // SecurityContext에 인증 정보가 설정되었는지 확인
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(authUser);
    }

    @Test
    @DisplayName("인증이 불필요한 경로는 필터 스킵")
    void doFilterInternal_인증불필요경로_필터스킵() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/auth/login");

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, authUserService);
    }

    @Test
    @DisplayName("에러 경로는 필터 스킵")
    void doFilterInternal_에러경로_필터스킵() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/error");

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, authUserService);
    }

    @Test
    @DisplayName("만료된 JWT 토큰 처리")
    void doFilterInternal_만료된토큰_예외처리() throws Exception {
        // given
        String expiredToken = "expired.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singletonList("Authorization")));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtUtil.resolveAccessToken(request)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(request).setAttribute("jwt-exception", "토큰 만료");
        verifyNoInteractions(authUserService);
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 때 예외 발생")
    void doFilterInternal_유효하지않은토큰_예외발생() throws Exception {
        // given
        String invalidToken = "invalid.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singletonList("Authorization")));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.resolveAccessToken(request)).thenReturn(invalidToken);
        when(jwtUtil.isTokenValid(invalidToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> jwtAuthFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("사용자를 찾을 수 없을 때 예외 발생")
    void doFilterInternal_사용자없음_예외발생() throws Exception {
        // given
        String validToken = "valid.jwt.token";
        String email = "nonexistent@example.com";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singletonList("Authorization")));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.resolveAccessToken(request)).thenReturn(validToken);
        when(jwtUtil.isTokenValid(validToken)).thenReturn(true);
        when(jwtUtil.getEmail(validToken)).thenReturn(email);
        when(authUserService.loadAuthUserByEmail(email)).thenThrow(new RuntimeException("User not found"));

        // when & then
        assertThatThrownBy(() -> jwtAuthFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 필터 통과")
    void doFilterInternal_헤더없음_필터통과() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(jwtUtil.resolveAccessToken(request)).thenThrow(new TokenException(TokenErrorCode.TOKEN_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> jwtAuthFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }
} 