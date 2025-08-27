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
    private static String domain;

    public static ResponseCookie createTokenCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(true)
                .domain(domain)
                .path("/")
                .sameSite("none")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        return cookie;
    }

    public static ResponseCookie deleteTokenCookie(String name) {
        return ResponseCookie.from(name, "")
                .secure(true)
                .domain(domain)
                .path("/")
                .sameSite("none")
                .maxAge(0)  // 즉시 만료
                .build();
    }
}

