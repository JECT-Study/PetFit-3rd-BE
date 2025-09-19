package ject.petfit.domain.routine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.routine.dto.request.RoutineMemoRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.facade.RoutineFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
@Tag(name = "Routine", description = "루틴 API <br> " +
        "활성화되어 있는 슬롯의 루틴만 조작 가능 <br> " +
        "{category}는 루틴 종류 feed, water, walk, potty, (dental, skin), supplement, medicine, custom1, custom2, custom3 중 하나 입력")
public class RoutineController {
    private final RoutineFacade routineFacade;

    // 일간 루틴 조회
    @GetMapping("/{petId}/daily/{date}")
    @Operation(summary = "일간 루틴 조회", description = "오늘 혹은 과거 날짜의 루틴들 상태 조회 <br> " +
            "미래에 대한 조회는 막아둠 <br>" +
            "실제 사용은 홈화면 오늘의 루틴 조회 용도 / 과거의 루틴은 QA용")
    public ResponseEntity<ApiResponse<List<RoutineResponse>>> getDailyRoutines(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-01")
            @PathVariable LocalDate date
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(routineFacade.getDailyRoutines(petId, date))
        );
    }

    // 루틴 체크(V)
    @PostMapping("/{petId}/{date}/{category}/check")
    @Operation(summary = "루틴 체크", description = "루틴을 체크 상태로 변경")
    public ResponseEntity<ApiResponse<String>> checkRoutine(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-01")
            @PathVariable LocalDate date,
            @Parameter(description = "루틴 종류 - feed, water, walk, potty, (dental, skin), supplement, medicine, custom1, custom2, custom3", example = "feed")
            @PathVariable String category
//            @RequestBody RoutineRequest routineRequest
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(routineFacade.checkRoutine(petId, date, category))
        );
    }

    // 루틴 세모
    @PostMapping("/{petId}/{date}/{category}/memo")
    @Operation(summary = "루틴 메모(세모)", description = "루틴을 메모 상태로 작성 <br> " +
            "content가 산책, 사료, 음수, 영양제, 약, 커스텀 일때는 내용 | 배변 일때는 메모로 사용 <br>" +
            "actualAmount는 배변, 커스텀에서 0~99999 이내 아무값이나 입력" )
    public ResponseEntity<ApiResponse<RoutineResponse>> createRoutineMemo(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-01")
            @PathVariable LocalDate date,
            @Parameter(description = "루틴 종류 - feed, water, walk, potty, (dental, skin), supplement, medicine, custom1, custom2, custom3", example = "feed")
            @PathVariable String category,
            @Valid @RequestBody RoutineMemoRequest routineMemoRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(routineFacade.addMemoRoutine(petId, date, category, routineMemoRequest))
        );
    }

    // 루틴 미체크
    @DeleteMapping("/{petId}/{date}/{category}/uncheck")
    @Operation(summary = "루틴 미체크", description = "루틴을 미체크 상태로 변경")
    public ResponseEntity<ApiResponse<String>> uncheckRoutine(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-01")
            @PathVariable LocalDate date,
            @Parameter(description = "루틴 종류 - feed, water, walk, potty, (dental, skin), supplement, medicine, custom1, custom2, custom3", example = "feed")
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(routineFacade.uncheckRoutine(petId, date, category))
        );
    }



}
