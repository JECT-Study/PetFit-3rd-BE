package ject.petfit.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.dto.RefreshTokenRequestDTO;
import ject.petfit.global.jwt.exception.TokenErrorCode;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import ject.petfit.global.jwt.refreshtoken.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(JwtTestConfig.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtUtil jwtUtil;


    private AuthUser authUser;
    private Member member;

    @MockitoBean
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
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

        refreshToken = new RefreshToken(authUser, "hashed.refresh.token", java.time.Instant.now().plusSeconds(3600));
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 새 토큰 발급 성공")
    void refresh_유효한리프레시토큰_새토큰발급성공() throws Exception {
        // given
        String oldRefreshToken = "old.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshTokenValue = "new.refresh.token";

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(oldRefreshToken);

        when(refreshTokenService.validateAndRotateToken(oldRefreshToken)).thenReturn(authUser);
        when(jwtUtil.createAccessToken(anyString(), anyString())).thenReturn(newAccessToken);
        when(refreshTokenService.createOrUpdateRefreshToken(any(), anyString(), anyLong()))
                .thenReturn(refreshToken);
        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getToken()).thenReturn(newRefreshTokenValue);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 예외 발생")
    void refresh_유효하지않은토큰_예외발생() throws Exception {
        // given
        String invalidToken = "invalid.token";
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(invalidToken);

        when(refreshTokenService.validateAndRotateToken(invalidToken))
                .thenThrow(new TokenException(TokenErrorCode.REFRESH_TOKEN_INVALID));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰으로 예외 발생")
    void refresh_존재하지않는토큰_예외발생() throws Exception {
        // given
        String nonExistentToken = "nonexistent.token";
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(nonExistentToken);

        when(refreshTokenService.validateAndRotateToken(nonExistentToken))
                .thenThrow(new TokenException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 예외 발생")
    void refresh_만료된토큰_예외발생() throws Exception {
        // given
        String expiredToken = "expired.token";
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(expiredToken);

        when(refreshTokenService.validateAndRotateToken(expiredToken))
                .thenThrow(new TokenException(TokenErrorCode.REFRESH_TOKEN_EXPIRED));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TokenNullOrEmptyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("빈 리프레시 토큰으로 요청 시 400 에러")
    void refresh_빈토큰_400에러() throws Exception {
        // given
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("");

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"\"}")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("리프레시 토큰 필드가 없을 때 400 에러")
    void refresh_토큰필드없음_400에러() throws Exception {
        // given
        String requestJson = "{}";

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }
}