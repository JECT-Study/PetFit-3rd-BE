package ject.petfit.domain.user.controller;


import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthService;
import ject.petfit.domain.user.dto.request.AuthUserRequestDTO;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final AuthService authService;

    @GetMapping("/auth/kakao/login")
    public ResponseEntity<AuthUserResponseDTO.JoinResultDTO> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        AuthUser user = authService.oAuthLogin(accessCode, httpServletResponse);
        AuthUserResponseDTO.JoinResultDTO dto = AuthUserConverter.toJoinResultDTO(user);
        // 이부분 단순 ok 처리 no
        // join에 맞게 dto 추가 수정 필요
        return ResponseEntity.ok(dto);
    }
}
