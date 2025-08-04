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
    private static String frontDomain;

    public static void addCookie(String name, String value, HttpServletResponse response) {
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

    public static Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(true);
        cookie.setDomain(frontDomain);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }

    public static ResponseCookie createTokenCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(true)
                .domain(frontDomain)
                .path("/")
                .sameSite("none")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        return cookie;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            return;
        }
        for(Cookie cookie : cookies){
            if(name.equals(cookie.getName())){
                cookie.setValue(""); // 쿠키 빈 값 처리
                cookie.setPath("/");
                cookie.setMaxAge(0); // 만료 시간 0으로 바로 만료되도록
                response.addCookie(cookie);
            }
        }
    }

    public static Cookie deleteCookieByName(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    public static String getCookieValue(HttpServletRequest req, String name) {
        return Optional
                .ofNullable(req.getCookies())
                .flatMap(cookies ->
                        Arrays.stream(cookies)
                                .filter(cookie -> cookie.getName().equals(name))
                                .map(Cookie::getValue)
                                .filter(value -> !value.isBlank())
                                .findFirst()
                )
                .orElse(null);
    }
}

