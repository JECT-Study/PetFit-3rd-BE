package ject.petfit.domain.member.facade;

import ject.petfit.domain.member.dto.request.MemberRequestDto;
import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.service.MemberCommandService;
import ject.petfit.domain.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    public MemberResponseDto getMemberById(Long memberId) {
        return memberQueryService.getMemberById(memberId);
    }

    public MemberResponseDto getMemberByEmail(String email) {
        return memberQueryService.getMemberByAuthUserEmail(email);
    }

    public MemberResponseDto updateMember(Long memberId, MemberRequestDto command) {
        return memberCommandService.updateMember(memberId, command);
    }
} 