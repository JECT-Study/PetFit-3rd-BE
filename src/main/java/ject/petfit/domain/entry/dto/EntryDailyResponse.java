package ject.petfit.domain.entry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "달력에서 기록 주간 응답 DTO")
public class EntryDailyResponse {
    @Schema(description = "날짜", example = "2025-07-01")
    private String entryDate; // 날짜

    @Schema(description = "루틴체크 존재 여부", example = "true")
    private boolean isChecked; // 루틴체크 존재 여부

    @Schema(description = "메모 존재 여부", example = "true")
    private boolean isMemo; // 메모 존재 여부

    @Schema(description = "특이사항 존재 여부", example = "true")
    private boolean isRemarked; // 특이사항 존재 여부

    @Schema(description = "일정 존재 여부", example = "false")
    private boolean isScheduled; // 일정 존재 여부

    List<RoutineResponse> routineResponseList;
    List<RemarkResponse> remarkResponseList;

    public static EntryDailyResponse from(Entry entry) {
        return EntryDailyResponse.builder()
                .entryDate(entry.getEntryDate().toString())
                .isChecked(entry.getIsChecked())
                .isMemo(entry.getIsMemo())
                .isRemarked(entry.getIsRemarked())
                .isScheduled(entry.getIsScheduled())
                .routineResponseList(entry.getRoutines().stream()
                        .map(RoutineResponse::from)
                        .toList())
                .remarkResponseList(entry.getRemarks().stream()
                        .map(RemarkResponse::from)
                        .toList())
                .build();
    }
}
