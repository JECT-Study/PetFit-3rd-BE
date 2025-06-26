package ject.petfit.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenRepository;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class JwtIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private AuthUser authUser;
    private Member member;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

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

        authUser = authUserRepository.save(authUser);
    }

    @Test
    @DisplayName("JWT 토큰 생성 및 검증 통합 테스트")
    void jwtToken_생성및검증_통합테스트() {
        // given
        String email = authUser.getEmail();
        String role = authUser.getMember().getRole().name();

        // when
        String accessToken = jwtUtil.createAccessToken(email, role);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(jwtUtil.isTokenValid(accessToken)).isTrue();
        assertThat(jwtUtil.getEmail(accessToken)).isEqualTo(email);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 검증 통합 테스트")
    void refreshToken_생성및검증_통합테스트() {
        // given
        String rawToken = "test.refresh.token";
        long validitySeconds = 3600L;

        // when
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, rawToken, validitySeconds);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getAuthUser()).isEqualTo(authUser);
        assertThat(refreshToken.getExpires_at()).isAfter(java.time.Instant.now());

        // 검증 및 회전 테스트
        AuthUser validatedUser = refreshTokenService.validateAndRotateToken(rawToken);
        assertThat(validatedUser).isEqualTo(authUser);
    }

    @Test
    @DisplayName("토큰 갱신 API 통합 테스트")
    void refreshToken_API_통합테스트() throws Exception {
        // given
        String rawRefreshToken = "test.refresh.token";
        long validitySeconds = 3600L;

        // 리프레시 토큰 생성
        refreshTokenService.createOrUpdateRefreshToken(authUser, rawRefreshToken, validitySeconds);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(rawRefreshToken);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 갱신 시도 시 실패")
    void refreshToken_만료된토큰_갱신실패() throws Exception {
        // given
        String expiredToken = "expired.refresh.token";
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(expiredToken);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("리프레시 토큰 회전 후 기존 토큰 무효화 테스트")
    void refreshToken_회전후기존토큰무효화_테스트() {
        // given
        String rawToken = "test.refresh.token";
        long validitySeconds = 3600L;

        // 리프레시 토큰 생성
        refreshTokenService.createOrUpdateRefreshToken(authUser, rawToken, validitySeconds);

        // when - 토큰 회전
        refreshTokenService.validateAndRotateToken(rawToken);

        // then - 기존 토큰으로 다시 검증 시도하면 실패
        assertThat(refreshTokenRepository.findByToken(rawToken)).isEmpty();
    }

    @Test
    @DisplayName("동일한 사용자에 대한 리프레시 토큰 업데이트 테스트")
    void refreshToken_동일사용자토큰업데이트_테스트() {
        // given
        String firstToken = "first.refresh.token";
        String secondToken = "second.refresh.token";
        long validitySeconds = 3600L;

        // 첫 번째 토큰 생성
        RefreshToken firstRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, firstToken, validitySeconds);

        // when - 두 번째 토큰으로 업데이트
        RefreshToken secondRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, secondToken, validitySeconds);

        // then - 동일한 엔티티가 업데이트되었는지 확인
        assertThat(secondRefreshToken).isEqualTo(firstRefreshToken);
        assertThat(refreshTokenRepository.findByAuthUser(authUser)).isPresent();
    }
} 