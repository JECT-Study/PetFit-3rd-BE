package ject.petfit.global.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        // Mock 설정 초기화
    }

    @Test
    @DisplayName("쿠키 생성 성공")
    void addCookie_성공() {
        // given
        String name = "test_cookie";
        String value = "test_value";

        // when
        Cookie cookie = CookieUtils.addCookie(name, value);

        // then
        assertThat(cookie.getName()).isEqualTo(name);
        assertThat(cookie.getValue()).isEqualTo(value);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(7 * 24 * 60 * 60); // 7일
    }

    @Test
    @DisplayName("빈 값으로 쿠키 생성 시 빈 값 설정")
    void addCookie_빈값_빈값설정() {
        // given
        String name = "test_cookie";
        String emptyValue = "";

        // when
        Cookie cookie = CookieUtils.addCookie(name, emptyValue);

        // then
        assertThat(cookie.getName()).isEqualTo(name);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    @DisplayName("null 값으로 쿠키 생성 시 null 값 설정")
    void addCookie_null값_null값설정() {
        // given
        String name = "test_cookie";
        String nullValue = null;

        // when
        Cookie cookie = CookieUtils.addCookie(name, nullValue);

        // then
        assertThat(cookie.getName()).isEqualTo(name);
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    @DisplayName("쿠키 삭제 성공 - 해당 쿠키가 존재하는 경우")
    void deleteCookie_존재하는쿠키_삭제성공() {
        // given
        String cookieName = "test_cookie";
        Cookie existingCookie = new Cookie(cookieName, "test_value");
        Cookie[] cookies = {existingCookie};

        when(request.getCookies()).thenReturn(cookies);

        // when
        CookieUtils.deleteCookie(request, response, cookieName);

        // then
        verify(response).addCookie(any(Cookie.class));
        assertThat(existingCookie.getValue()).isEmpty();
        assertThat(existingCookie.getMaxAge()).isEqualTo(0);
        assertThat(existingCookie.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("쿠키 삭제 - 해당 쿠키가 존재하지 않는 경우")
    void deleteCookie_존재하지않는쿠키_아무동작안함() {
        // given
        String cookieName = "test_cookie";
        String differentCookieName = "different_cookie";
        Cookie differentCookie = new Cookie(differentCookieName, "test_value");
        Cookie[] cookies = {differentCookie};

        when(request.getCookies()).thenReturn(cookies);

        // when
        CookieUtils.deleteCookie(request, response, cookieName);

        // then
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("쿠키 삭제 - 쿠키 배열이 null인 경우")
    void deleteCookie_쿠키배열null_아무동작안함() {
        // given
        String cookieName = "test_cookie";
        when(request.getCookies()).thenReturn(null);

        // when
        CookieUtils.deleteCookie(request, response, cookieName);

        // then
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("쿠키 삭제 - 빈 쿠키 배열인 경우")
    void deleteCookie_빈쿠키배열_아무동작안함() {
        // given
        String cookieName = "test_cookie";
        Cookie[] cookies = {};

        when(request.getCookies()).thenReturn(cookies);

        // when
        CookieUtils.deleteCookie(request, response, cookieName);

        // then
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("여러 쿠키 중 특정 쿠키만 삭제")
    void deleteCookie_여러쿠키중특정쿠키만삭제() {
        // given
        String targetCookieName = "target_cookie";
        String otherCookieName = "other_cookie";
        
        Cookie targetCookie = new Cookie(targetCookieName, "target_value");
        Cookie otherCookie = new Cookie(otherCookieName, "other_value");
        Cookie[] cookies = {targetCookie, otherCookie};

        when(request.getCookies()).thenReturn(cookies);

        // when
        CookieUtils.deleteCookie(request, response, targetCookieName);

        // then
        verify(response).addCookie(any(Cookie.class));
        assertThat(targetCookie.getValue()).isEmpty();
        assertThat(targetCookie.getMaxAge()).isEqualTo(0);
        assertThat(otherCookie.getValue()).isEqualTo("other_value"); // 다른 쿠키는 변경되지 않음
    }
} 