package ject.petfit.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import ject.petfit.domain.member.dto.MemberResponseDto;
import ject.petfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    @Operation(summary = "마이페이지 닉네임 조회", description = "회원 ID로 닉네임 조회")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long memberId) {
        MemberResponseDto member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/{memberId}")
    @Operation(summary = "마이페이지 닉네임 수정", description = "회원 ID로 닉네임 수정")
    public ResponseEntity<MemberResponseDto> editMemberNickNameById(@PathVariable Long memberId) {
        MemberResponseDto editedMember = memberService.editMember(memberId);
        return ResponseEntity.ok(editedMember);
    }
}
