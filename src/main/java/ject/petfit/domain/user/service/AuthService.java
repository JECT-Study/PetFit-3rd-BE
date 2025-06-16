package ject.petfit.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.common.util.KakaoUtil;
import ject.petfit.domain.user.dto.KakaoDTO;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.common.util.JwtUtil;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final AuthUserRepository authUserRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthUser oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);

        // custom error handling for missing kakaoProfile 처리 필
        String email = kakaoProfile.getKakaoAccount().getEmail();

        AuthUser user = authUserRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(kakaoProfile));

        String token = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        httpServletResponse.setHeader("Authorization", token);

        return user;
    }

    private AuthUser createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        // error handling for missing profile information
        if (kakaoProfile == null || kakaoProfile.getKakaoAccount() == null) {
            throw new IllegalArgumentException("카카오 프로필 정보가 부족합니다.");
        }

        Member newMember = Member.builder()
                .nickname(kakaoProfile.getProperties().getNickname())
                .role(Role.USER)
                .build();
        memberRepository.save(newMember);

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        AuthUser newUser = AuthConverter.toUser(
                kakaoProfile.getId(),
                kakaoProfile.getKakaoAccount().getEmail(),
                kakaoProfile.getProperties().getNickname(),
                encodedPassword,
                LocalDateTime.now()
        );
        newUser.addMember(newMember);

        return authUserRepository.save(newUser);
    }
}
