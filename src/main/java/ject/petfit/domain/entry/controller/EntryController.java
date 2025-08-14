package ject.petfit.domain.entry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.entry.dto.EntryDailyResponse;
import ject.petfit.domain.entry.dto.EntryExistsResponse;
import ject.petfit.domain.entry.facade.EntryFacade;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.service.RoutineService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entries")
@Tag(name = "달력 API")
public class EntryController {
    private final EntryFacade entryFacade;
    private final EntryService entryService;
    private final RoutineService routineService;

    // 월간 루틴체크,메모,특이사항,(일정) 유무 조회
    @GetMapping("/{petId}/monthly/{month}")
    @Operation(summary = "월간 루틴완료,메모,특이사항,(일정) 유무 조회",
            description = "특정 월의 루틴완료, 메모, 특이사항, (일정) 유무를 조회 <br> " +
                    "요구사항은 아니지만 일정 유무도 응답에 포함 ")
    public ResponseEntity<ApiResponse<List<EntryExistsResponse>>> getMonthlyEntries(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM 형식으로 입력", example = "2025-07")
            @PathVariable String month
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(entryFacade.getMonthlyEntries(petId, month))
        );
    }

    // 주간 루틴 상태, 특이사항 조회
//    @GetMapping("/{petId}/weekly/{week}")
//    @Operation(summary = "주간 루틴 상태, 특이사항 조회",
//            description = "특정 주의 루틴 완료, 메모, 특이사항을 조회 <br> " +
//                    "해당 주의 루틴 체크, 메모, 특이사항을 모두 조회 <br> " +
//                    "월간 조회처럼 유무 값도 포함 <br>" +
//                    "주간 조회 응답에서 일간 조회 뽑아서 사용하면 API 요청을 줄일 수 있으니 일간 조회API를 따로 만들지 않았음")
//    public ResponseEntity<ApiResponse<List<EntryDailyResponse>>> getWeeklyEntries(  //
//            @PathVariable Long petId,
//            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-07")
//            @PathVariable String week  // 주간의 시작 날짜
//    ) {
//        return ResponseEntity.ok(
//                ApiResponse.success(entryService.getWeeklyEntries(petId, week))
//        );
//    }

    // 일간 특이사항 + 루틴 리스트 조회
    @GetMapping("/{petId}/daily/{date}")
    @Operation(summary = "일간 특이사항 리스트 + 루틴 리스트 조회")
    public ResponseEntity<ApiResponse<EntryDailyResponse>> getDailyEntries(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-01")
            @PathVariable LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(entryFacade.getDailyEntries(petId, date))
        );
    }

}

