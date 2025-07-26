package ject.petfit.domain.routine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.routine.dto.request.RoutineUpdateRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.service.PastRoutineService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
@Tag(name = "과거의 루틴 API", description = "개발중")
public class PastRoutineController {
    private final PastRoutineService pastRoutineService;

    // 과거의 루틴 생성 (현재 활성화 슬롯으로)
//    @PostMapping("/{petId}/{date}/create")

    // 과거의 루틴 수정
    @PatchMapping("/{date}/{routineId}")
    @Operation(summary = "과거의 루틴 수정", description = "과거 날짜의 루틴을 수정 <br> " +
            "{category}는 루틴 종류 - feed, water, walk, potty, dental, skin <br>" +
            "과거의 루틴만 수정 가능, 오늘 이후 날짜에는 요청 불가")
    public ResponseEntity<ApiResponse<RoutineResponse>> updateRoutine(
            @Parameter(description = "루틴ID", example = "2")
            @PathVariable Long routineId,
            @Valid @RequestBody RoutineUpdateRequest routineUpdateRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(pastRoutineService.updateRoutine(routineId, routineUpdateRequest))
        );
    }
}
