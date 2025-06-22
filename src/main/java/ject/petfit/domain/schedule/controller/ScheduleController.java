package ject.petfit.domain.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.schedule.dto.request.ScheduleRegisterRequest;
import ject.petfit.domain.schedule.dto.request.ScheduleUpdateRequest;
import ject.petfit.domain.schedule.dto.response.ScheduleResponse;
import ject.petfit.domain.schedule.service.ScheduleService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "일정(알람) API")
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 일정 조회(3일치)
    @GetMapping("/{petId}/home")
    @Operation(summary = "홈화면 일정 조회", description = "홈화면에서 3일치 일정들을 조회")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getHomeSchedule(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(scheduleService.getHomeSchedule(petId, 3))
        );
    }

    // 일정 조회(All)
    @GetMapping("/{petId}/all")
    @Operation(summary = "모든 일정 조회", description = "알람 설정 화면에서 등록된 모든 일정 리스트 조회")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getScheduleList(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(scheduleService.getScheduleList(petId))
        );
    }

    // 일정 단일 조회 - 필요?

    // 일정 등록
    // 현재는 '일'까지만 요청, 추후 '시분초'까지 요청 가능하도록 변경할지 검토 필요
    @PostMapping("/{petId}")
    @Operation(summary = "일정 등록", description = "title(20자), content(200자), targetDate(YYYY-MM-DD) 형식 제한")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @PathVariable Long petId,
            @RequestBody @Valid ScheduleRegisterRequest scheduleRegisterRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(scheduleService.createSchedule(petId, scheduleRegisterRequest))
        );
    }

    // 일정 수정
    @PatchMapping("/{scheduleId}")
    @Operation(summary = "일정 수정", description = "일정 ID로 제목이나 내용 수정")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest scheduleUpdateRequest) {
        return ResponseEntity.ok(
                ApiResponse.success(scheduleService.updateSchedule(scheduleId, scheduleUpdateRequest))
        );
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "일정 ID로 일정 삭제")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("일정 삭제 성공"));
    }
}
