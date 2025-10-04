package ject.petfit.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import ject.petfit.domain.member.dto.request.MemberRequestDto;
import ject.petfit.domain.member.dto.response.MemberResponseDto;
import ject.petfit.domain.member.facade.MemberFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @GetMapping
    @Operation(summary = "마이페이지 닉네임 조회", description = "회원 ID로 닉네임 조회")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberById(
            @CookieValue (name = "access_token", required = false) String accessToken
    ) {
        MemberResponseDto member = memberFacade.getMemberByAccessToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(member)
        );
    }

    @PutMapping
    @Operation(summary = "마이페이지 닉네임 수정", description = "회원 ID로 닉네임 수정")
    public ResponseEntity<ApiResponse<MemberResponseDto>> editMemberNickNameById(
            @CookieValue (name = "access_token", required = false) String accessToken,
            @Valid @RequestBody MemberRequestDto command
    ) {
        MemberResponseDto editedMember = memberFacade.updateMember(accessToken, command);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(editedMember)
        );
    }
}
