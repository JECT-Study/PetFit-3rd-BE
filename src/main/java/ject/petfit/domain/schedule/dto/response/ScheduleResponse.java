package ject.petfit.domain.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일정 응답 DTO")
public class ScheduleResponse {
    @Schema(description = "일정 ID", example = "1")
    private Long scheduleId; // 일정 ID

    @Schema(description = "일정 제목", example = "병원")
    private String title; // 일정 제목

    @Schema(description = "일정 내용", example = "제일 조은 동물병원, 치석 제거")
    private String content; // 일정 내용

    @Schema(description = "대상 날짜 (YYYY-MM-DD)", example = "2025-07-01")
    private String targetDate; // 대상 날짜 (YYYY-MM-DD)

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .targetDate(schedule.getTargetDate().toLocalDate().toString())
                .build();
    }
}
