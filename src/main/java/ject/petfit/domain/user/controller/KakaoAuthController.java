package ject.petfit.domain.user.controller;


import jakarta.servlet.http.HttpServletResponse;
import ject.petfit.domain.user.converter.AuthUserConverter;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.service.AuthService;
import ject.petfit.domain.user.dto.response.AuthUserResponseDTO;
import ject.petfit.global.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @GetMapping("/auth/kakao/login")
    public ResponseEntity<AuthUserResponseDTO.JoinResultDTO> kakaoLogin(
            @RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        AuthUser user = authService.oAuthLogin(accessCode, httpServletResponse);

        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getMember().getRole().toString());
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

        AuthUserResponseDTO.JoinResultDTO dto = AuthUserConverter.toJoinResultDTO(user, accessToken);

        return ResponseEntity.ok(dto);
    }
}
