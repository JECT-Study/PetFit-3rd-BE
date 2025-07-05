package ject.petfit.global.jwt.refreshtoken;

import java.util.List;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.exception.TokenException;
import ject.petfit.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private AuthUser authUser;
    private Member member;

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

        MockitoAnnotations.openMocks(this);
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
        refreshTokenService.setPasswordEncoder(passwordEncoder); // 직접 주입
    }

    @Test
    @DisplayName("새로운 리프레시 토큰 생성 성공")
    void createOrUpdateRefreshToken_새토큰생성_성공() {
        // given
        String rawToken = "new.refresh.token";
        String hashedToken = "hashed.refresh.token";
        long validitySeconds = 3600L;

        when(refreshTokenRepository.findByAuthUser(authUser)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawToken)).thenReturn(hashedToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        RefreshToken result = refreshTokenService.createOrUpdateRefreshToken(authUser, rawToken, validitySeconds);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthUser()).isEqualTo(authUser);
        assertThat(result.getToken()).isEqualTo(hashedToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("기존 리프레시 토큰 업데이트 성공")
    void createOrUpdateRefreshToken_기존토큰업데이트_성공() {
        // given
        String rawToken = "updated.refresh.token";
        String hashedToken = "hashed.updated.token";
        long validitySeconds = 3600L;
        RefreshToken existingToken = new RefreshToken(authUser, "old.hashed.token", Instant.now());

        when(refreshTokenRepository.findByAuthUser(authUser)).thenReturn(Optional.of(existingToken));
        when(passwordEncoder.encode(rawToken)).thenReturn(hashedToken);

        // when
        RefreshToken result = refreshTokenService.createOrUpdateRefreshToken(authUser, rawToken, validitySeconds);

        // then
        assertThat(result).isEqualTo(existingToken);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("유효한 리프레시 토큰 검증 및 회전 성공")
    void validateAndRotateToken_유효한토큰_성공() {
        // given
        String rawToken = "valid.refresh.token";
        String testToken = "hashed.valid.token";
        RefreshToken refreshToken = new RefreshToken(authUser, testToken, Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findAll()).thenReturn(List.of(refreshToken));
        when(passwordEncoder.matches(rawToken, testToken)).thenReturn(true);

        // when
        AuthUser result = refreshTokenService.validateAndRotateToken(rawToken);

        // then
        assertThat(result).isEqualTo(authUser);
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하지 않을 때 예외 발생")
    void validateAndRotateToken_토큰없음_예외발생() {
        // given
        String invalidToken = "invalid.token";
        // findAll()이 빈 리스트를 반환하도록 stub
        when(refreshTokenRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.validateAndRotateToken(invalidToken))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-471");
    }

    @Test
    @DisplayName("리프레시 토큰이 유효하지 않을 때 예외 발생")
    void validateAndRotateToken_유효하지않은토큰_예외발생() {
        // given
        String rawToken = "invalid.refresh.token";
        String hashedToken = "hashed.valid.token";
        RefreshToken refreshToken = new RefreshToken(authUser, hashedToken, Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findAll()).thenReturn(List.of(refreshToken));
        when(passwordEncoder.matches(rawToken, hashedToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> refreshTokenService.validateAndRotateToken(rawToken))
                .isInstanceOf(TokenException.class)
                .hasFieldOrPropertyWithValue("code", "TOKEN-471");
    }

} 