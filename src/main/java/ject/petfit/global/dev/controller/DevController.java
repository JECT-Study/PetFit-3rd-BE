package ject.petfit.global.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.dev.dto.EntryFlushResponse;
import ject.petfit.global.dev.dto.TokenResponse;
import ject.petfit.global.dev.service.DevService;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import ject.petfit.global.kafka.ConsumerService;
import ject.petfit.global.kafka.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@Slf4j
@RestController
@Tag(name = "Developer", description = "개발자용 API")
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {
    private final AuthUserRepository authUserRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final DevService devService;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    @Operation(summary = "토큰 발급 API ",
            description = "준비된 테스트 계정으로 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @GetMapping("/login/{testId}")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Parameter(description = "테스트용 사용자 ID (0, -1, -2,... -10)까지 준비되어있음 <br>" +
                    "0번 계정은 슬롯 활성화되어있으며 해당 계정에 연결된 petId가 1~10번 (루틴 기록 시나리오 테스트용) <br>" +
                    "-1 ~ -10번 계정은 멤버 정보까지만 기입되어있으며 맨처음 온보딩(동물 등록과 슬롯 초기화)부터 진행해야함 ")
            @PathVariable Long testId
    ) {
        AuthUser authUser = authUserRepository.findById(testId)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(authUser.getEmail(), authUser.getMember().getRole().name());
        String newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, UUID.randomUUID().toString(),
                refreshTokenValiditySeconds).getToken();

        return ResponseEntity.ok(
                ApiResponse.success(new TokenResponse(newAccessToken, newRefreshToken))
        );
    }

    @PostMapping("/flush")
    @Operation(summary = "하루 기록 수동 업데이트",
            description = "1. 해당 날짜의 루틴 완료 여부 업데이트 <br>" +
                    "2. 해당 날짜의 미체크 루틴을 DB에 추가 <br> " +
                    "기록된 루틴 CHECKED, 루틴 MEMO, 특이사항, 일정이 있는 경우에만 업데이트")
    public ResponseEntity<ApiResponse<EntryFlushResponse>> flushEntries(
            @RequestParam LocalDate entryDate,
            @RequestParam Long petId
    ) {
        devService.entryDateFlush(petId, entryDate);
        return ResponseEntity.ok(
                ApiResponse.success(devService.getEntryFlushResponse(petId, entryDate))
        );
    }
}
