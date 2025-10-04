package ject.petfit.domain.member.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.member.dto.request.MemberRequestDto;
import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public MemberResponseDto updateMember(String accessToken, MemberRequestDto command) {
        Long memberId = jwtUtil.getMemberId(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.editNickname(command.getNickname());

        AuthUser authUser = member.getAuthUser();
        if (authUser == null) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        authUser.editNickname(command.getNickname());

        return new MemberResponseDto(member.getNickname(), member.getRole());
    }
} 