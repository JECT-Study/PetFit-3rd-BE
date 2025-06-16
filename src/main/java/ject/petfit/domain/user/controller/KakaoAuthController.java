package ject.petfit.domain.user.controller;


import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.member.entity.Member;
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

    @PostMapping("/login")
    public ResponseEntity<?> join(@RequestBody UserRequestDTO.LoginRequestDTO loginRequestDTO) {
        return null;
    }

    @GetMapping("/auth/kakao/login")
    public BaseResponse<UserResponseDTO.JoinResultDTO> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        Member member = authService.oAuthLogin(accessCode, httpServletResponse);
        return BaseResponse.onSuccess(UserConverter.toJoinResultDTO(user));
    }
}
