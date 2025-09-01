package ject.petfit.domain.member.service;


import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberResponseDto getMemberById(Long memberId) {
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponseDto(member.getId(), member.getNickname(), member.getRole());
    }

    public MemberResponseDto getMemberByAuthUserEmail(String email) {
        
        Member member = memberRepository.findByAuthUserEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponseDto(member.getId(), member.getNickname(), member.getRole());
    }
} 