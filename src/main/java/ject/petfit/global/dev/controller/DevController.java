package ject.petfit.global.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.dev.dto.TokenResponse;
import ject.petfit.global.jwt.refreshtoken.service.RefreshTokenService;
import ject.petfit.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        String newAccessToken = jwtUtil.createAccessToken(
                authUser.getEmail(), authUser.getMember().getRole().name(), authUser.getMember().getId());
        String newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authUser, UUID.randomUUID().toString(),
                refreshTokenValiditySeconds).getToken();

        return ResponseEntity.ok(
                ApiResponse.success(new TokenResponse(newAccessToken, newRefreshToken))
        );
    }





//    @Operation(summary = "스웨거 동작 확인",
//            description = "상세 설명")
//    @GetMapping()
//    public ResponseEntity<String> swaggerTest() {
//        return ResponseEntity.ok("Swagger Test");
//    }
//
//    @Operation(summary = "ApiResponse 확인용 예제",
//            description = "ApiResponse 적용 후 성공/실패, 예외처리 응답 형태 확인")
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<String>> getUserName(@PathVariable Long id) {
//        if (id > 0) {
//            // 성공 응답
//            return ResponseEntity
//                    .ok(ApiResponse.success("김철수"));
//        }else if(id == 0){
//            // 실패 응답
//            return ResponseEntity
//                    .status(404)
//                    .body(ApiResponse.fail("DEV-404", "사용자를 찾을 수 없습니다(직접 기재)"));
//        }
//        // 예외처리 응답
//        throw new CustomException(ErrorCode.DEV_NOT_FOUND);
//    }

//    @PostMapping("/entries/{petId}/{entryDate}")
//    public ResponseEntity<ApiResponse<String>> createEntry(
//            @PathVariable Long petId,
//            @PathVariable String entryDate
//    ) {
//        Pet pet = petRepository.findById(petId)
//                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
//        entryService.createEntry(pet, LocalDate.parse(entryDate));
//        return ResponseEntity
//                .ok(ApiResponse.success("Entry 생성 성공"));
//    }
}
