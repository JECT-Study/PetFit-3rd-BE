package ject.petfit.domain.entry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.entry.dto.EntryDailyResponse;
import ject.petfit.domain.entry.dto.EntryExistsResponse;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entries")
@Tag(name = "Entry 수정중 - 루틴완료 로직 변경중, 일간 조회 추가예정", description = "달력에서 기록 조회 API")
public class EntryController {
    private final EntryService entryService;

    // 월간 루틴체크,메모,특이사항,(일정) 유무 조회
    @GetMapping("/{petId}/monthly/{month}")
    @Operation(summary = "월간 루틴체크,메모,특이사항,(일정) 유무 조회",
            description = "특정 월의 루틴 완료, 메모, 특이사항, (일정) 유무를 조회 <br> " +
                    "요구사항은 아니지만 일정 유무도 응답에 포함함 ")
    public ResponseEntity<ApiResponse<List<EntryExistsResponse>>> getMonthlyEntries(
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM 형식으로 입력", example = "2025-07")
            @PathVariable LocalDate month
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(entryService.getMonthlyEntries(petId, month))
        );
    }

    // 주간 루틴 상태, 특이사항 조회
    @GetMapping("/{petId}/weekly/{week}")
    @Operation(summary = "주간 루틴 상태, 특이사항 조회",
            description = "특정 주의 루틴 완료, 메모, 특이사항을 조회 <br> " +
                    "해당 주의 루틴 체크, 메모, 특이사항을 모두 조회 <br> " +
                    "월간 조회처럼 유무 값도 포함 <br>" +
                    "주간 조회 응답에서 일간 조회 뽑아서 사용하면 API 요청을 줄일 수 있으니 일간 조회API를 따로 만들지 않았음")
    public ResponseEntity<ApiResponse<List<EntryDailyResponse>>> getWeeklyEntries(  //
            @PathVariable Long petId,
            @Parameter(description = "yyyy-MM-dd 형식으로 입력", example = "2025-07-07")
            @PathVariable LocalDate week  // 주간의 시작 날짜
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(entryService.getWeeklyEntries(petId, week))
        );
    }

    // 일간 루틴부터 만들어야함 -> 주간 루틴 반영

}

