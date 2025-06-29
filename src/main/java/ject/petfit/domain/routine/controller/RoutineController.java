package ject.petfit.domain.routine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.routine.dto.request.RoutineMemoRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.service.RoutineService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
@Tag(name = "Routine", description = "홈화면 오늘의 루틴 API")
public class RoutineController {
    private final RoutineService routineService;

    // 루틴 조회 - 일간
    @GetMapping("/{petId}/daily/{date}")
    @Operation(summary = "일간 루틴 조회", description = "특정 날짜의 일간 루틴들을 조회 <br> " +
            "{date}는 yyyy-MM-dd 형식으로 입력 <br> " +
            "체크나 세모 등록된 값들만 응답" )
    public ResponseEntity<ApiResponse<List<RoutineResponse>>> getDailyRoutines(
            @PathVariable Long petId,
            @PathVariable String date
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(routineService.getDailyRoutines(petId, date))
        );
    }

    // 루틴 체크(V) 완료
    @PostMapping("/{petId}/{date}/{category}/check")
    @Operation(summary = "루틴 체크 완료", description = "루틴을 체크 완료 상태로 변경 <br> " +
            "{date}는 yyyy-MM-dd 형식으로 입력 <br>{category}는 루틴 종류 - feed, water, walk, potty, dental, skin")
    public ResponseEntity<ApiResponse<String>> checkRoutine(
            @PathVariable Long petId,
            @PathVariable String date,
            @PathVariable String category
//            @RequestBody RoutineRequest routineRequest
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(routineService.checkRoutine(petId, date, category))
        );
    }

    // 루틴 세모
    @PostMapping("/{petId}/{date}/{category}/memo")
    @Operation(summary = "루틴 메모(세모)", description = "루틴을 메모 상태로 작성 <br> " +
            "{date}는 yyyy-MM-dd 형식으로 입력 <br>{category}는 루틴 종류 - feed, water, walk, potty, dental, skin <br> " +
            "content가 산책, 사료, 음수일때는 내용 | 배변, 치아, 피부일때는 메모로 사용")
    public ResponseEntity<ApiResponse<RoutineResponse>> createRoutineMemo(
            @PathVariable Long petId,
            @PathVariable String date,
            @PathVariable String category,
            @Valid @RequestBody RoutineMemoRequest routineMemoRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(routineService.createRoutineMemo(petId, date, category, routineMemoRequest))
        );
    }

    // 루틴 해제
    @DeleteMapping("/{petId}/{date}/{category}/uncheck")
    @Operation(summary = "루틴 해제", description = "루틴을 해제(DB 삭제) <br> " +
            "{date}는 yyyy-MM-dd 형식으로 입력 <br>{category}는 루틴 종류 - feed, water, walk, potty, dental, skin")
    public ResponseEntity<ApiResponse<String>> uncheckRoutine(
            @PathVariable Long petId,
            @PathVariable String date,
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(routineService.uncheckRoutine(petId, date, category))
        );
    }
}
