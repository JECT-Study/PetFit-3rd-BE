package ject.petfit.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.UUID;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.common.util.KakaoUtil;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.dto.KakaoDTO;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final KakaoUtil kakaoUtil;
    private final AuthUserRepository authUserRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthUser oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        // custom error handling for missing kakaoProfile 처리 필
        String email = kakaoProfile.getKakaoAccount().getEmail();

        AuthUser user = authUserRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(accessCode, kakaoProfile));

        return user;
    }

    private AuthUser createNewUser(String accessCode, KakaoDTO.KakaoProfile kakaoProfile) {
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

        AuthUser newUser = AuthUserConverter.toUser(
                getKakaoUUID(accessCode),
                kakaoProfile.getKakaoAccount().getEmail(),
                kakaoProfile.getProperties().getNickname(),
                encodedPassword
        );
        newUser.addMember(newMember);

        return authUserRepository.save(newUser);
    }

    public Long getKakaoUUID(String accessCode) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode); // oAuthToken(access 토큰) 요청
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        return kakaoProfile.getId();
    }
}
