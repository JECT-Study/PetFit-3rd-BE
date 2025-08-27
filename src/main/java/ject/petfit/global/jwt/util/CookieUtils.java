package ject.petfit.global.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CookieUtils {

    @Value("${app.front.domain}")
    private String frontDomain;

    public void addCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(true);
        cookie.setDomain(frontDomain);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // 먼저 쿠키를 응답에 추가
        response.addCookie(cookie);

        // Set-Cookie 헤더를 읽고 SameSite=None 추가
        Collection<String> headers = response.getHeaders("Set-Cookie");
        boolean first = true;

        for (String header : headers) {
            if (!header.contains("SameSite")) {
                header = header + "; SameSite=None";
            }
            if (first) {
                response.setHeader("Set-Cookie", header);
                first = false;
            } else {
                response.addHeader("Set-Cookie", header);
            }
        }
    }

    public ResponseCookie createTokenCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(true)
                .domain(frontDomain)
                .path("/")
                .sameSite("none")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        return cookie;
    }

    public ResponseCookie deleteTokenCookie(String name) {
        return ResponseCookie.from(name, "")
                .secure(true)
                .domain(frontDomain)
                .path("/")
                .sameSite("none")
                .maxAge(0)  // 즉시 만료
                .build();
    }

}

