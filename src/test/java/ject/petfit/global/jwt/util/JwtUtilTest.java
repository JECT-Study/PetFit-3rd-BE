package ject.petfit.global.jwt.util;

import jakarta.servlet.http.HttpServletRequest;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-issuer", "test-secret-key", 3600000L);
    }

    @Test
    @DisplayName("액세스 토큰 생성 성공")
    void createAccessToken_성공() {
        // given
        String email = "test@example.com";
        String role = "USER";

        // when
        String token = jwtUtil.createAccessToken(email, role);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.getEmail(token)).isEqualTo(email);
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("요청 헤더에서 액세스 토큰 추출 성공")
    void resolveAccessToken_성공() {
        // given
        String validToken = "valid.jwt.token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // when
        String result = jwtUtil.resolveAccessToken(request);

        // then
        assertThat(result).isEqualTo(validToken);
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 예외 발생")
    void resolveAccessToken_토큰없음_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Authorization 헤더가 비어있을 때 예외 발생")
    void resolveAccessToken_헤더비어있음_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("");

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Authorization 헤더가 공백만 있을 때 예외 발생")
    void resolveAccessToken_헤더공백만있음_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("   ");

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Bearer 형식이 아닌 헤더일 때 예외 발생")
    void resolveAccessToken_Bearer형식아님_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Invalid " + "valid.jwt.token");

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Bearer 뒤에 토큰이 없을 때 예외 발생")
    void resolveAccessToken_Bearer뒤토큰없음_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("Bearer 뒤에 공백만 있을 때 예외 발생")
    void resolveAccessToken_Bearer뒤공백만있음_예외발생() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer    ");

        // when & then
        assertThatThrownBy(() -> jwtUtil.resolveAccessToken(request))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-401");
    }

    @Test
    @DisplayName("토큰 유효성 검증 성공")
    void isTokenValid_유효한토큰_성공() {
        // given
        String email = "test@example.com";
        String role = "USER";
        String token = jwtUtil.createAccessToken(email, role);

        // when
        boolean result = jwtUtil.isTokenValid(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void isTokenValid_만료된토큰_실패() {
        // given
        String expiredToken = "expired.jwt.token";

        // when
        boolean result = jwtUtil.isTokenValid(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 실패")
    void isTokenValid_잘못된형식토큰_실패() {
        // given
        String invalidToken = "invalid.token.format";

        // when
        boolean result = jwtUtil.isTokenValid(invalidToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰에서 이메일 추출 성공")
    void getEmail_성공() {
        // given
        String email = "test@example.com";
        String role = "USER";
        String token = jwtUtil.createAccessToken(email, role);

        // when
        String result = jwtUtil.getEmail(token);

        // then
        assertThat(result).isEqualTo(email);
    }
} 