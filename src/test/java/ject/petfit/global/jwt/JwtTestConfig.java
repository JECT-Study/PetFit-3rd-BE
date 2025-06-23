package ject.petfit.global.jwt;

import ject.petfit.global.jwt.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class JwtTestConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil("test-issuer", "test-secret-key", 3600000L);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 