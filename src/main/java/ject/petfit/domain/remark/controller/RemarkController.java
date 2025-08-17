package ject.petfit.domain.remark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.remark.dto.request.RemarkRegisterRequest;
import ject.petfit.domain.remark.dto.request.RemarkUpdateRequest;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import ject.petfit.domain.remark.facade.RemarkFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Remark", description = "특이사항 API")
@RequestMapping("/api/remarks")
public class RemarkController {
    private final RemarkFacade remarkFacade;

    // 홈화면 특이사항 조회(3일치)
    @GetMapping("/{petId}/home")
    @Operation(summary = "홈화면 특이사항 조회", description = "홈화면에서 3일치 특이사항을 조회")
    public ResponseEntity<ApiResponse<List<RemarkResponse>>> getHomeRemark(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(remarkFacade.getHomeRemark(petId))
        );
    }

    // 특이사항 단일 조회

    // 특이사항 등록
    @PostMapping("/{petId}")
    @Operation(summary = "특이사항 등록", description = "title(20자), content(200자), targetDate(YYYY-MM-DD) 형식 제한")
    public ResponseEntity<ApiResponse<RemarkResponse>> createRemark(
            @PathVariable Long petId,
            @RequestBody @Valid RemarkRegisterRequest remarkRegisterRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(remarkFacade.createRemark(petId, remarkRegisterRequest))
        );
    }

    // 특이사항 수정
    @PatchMapping("/{remarkId}")
    @Operation(summary = "특이사항 수정", description = "특이사항 ID로 내용 수정")
    public ResponseEntity<ApiResponse<RemarkResponse>> updateRemark(
            @PathVariable Long remarkId,
            @RequestBody @Valid RemarkUpdateRequest remarkRegisterRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(remarkFacade.updateRemark(remarkId, remarkRegisterRequest))
        );
    }

    // 특이사항 삭제
    @DeleteMapping("/{remarkId}")
    @Operation(summary = "특이사항 삭제", description = "특이사항 ID로 삭제")
    public ResponseEntity<ApiResponse<String>> deleteRemark(
            @PathVariable Long remarkId
    ) {
        remarkFacade.deleteRemark(remarkId);
        return ResponseEntity.ok(ApiResponse.success("특이사항 삭제 성공"));
    }
}
