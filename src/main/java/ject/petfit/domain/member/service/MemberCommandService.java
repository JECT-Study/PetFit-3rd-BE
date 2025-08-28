package ject.petfit.domain.member.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.member.dto.request.MemberRequestDto;
import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto command) {
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.editNickname(command.getNickname());

        AuthUser authUser = member.getAuthUser();
        if (authUser == null) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        authUser.editNickname(command.getNickname());

        return new MemberResponseDto(member.getId(), member.getNickname(), member.getRole());
    }
} 