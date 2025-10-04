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
import org.springframework.stereotype.Service;

// Facade Pattern + CQRS Pattern 도입으로 사용하지는 않으나 유지
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member.getNickname(), member.getRole());
    }

    @Transactional
    public MemberResponseDto editMember(Long memberId, MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.editNickname(memberRequestDto.getNickname());

        AuthUser authUser = member.getAuthUser();
        if (authUser == null) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        authUser.editNickname(memberRequestDto.getNickname());

        return new MemberResponseDto(member.getNickname(), member.getRole());
    }

}
