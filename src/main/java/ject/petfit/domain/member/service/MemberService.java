package ject.petfit.domain.member.service;


import ject.petfit.domain.member.dto.request.MemberRequestDto;
import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member.getId(), member.getNickname(), member.getRole());
    }

    public MemberResponseDto editMember(Long memberId, MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.editNickname(memberRequestDto.getNickname());
        Member updatedMember = memberRepository.save(member);
        return new MemberResponseDto(member.getId(), member.getNickname(), member.getRole());
    }
}
