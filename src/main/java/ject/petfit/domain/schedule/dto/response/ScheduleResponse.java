package ject.petfit.domain.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일정 응답 DTO")
public class ScheduleResponse {
    private Long scheduleId; // 일정 ID
    private String title; // 일정 제목
    private String content; // 일정 내용
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate; // 대상 날짜 (YYYY-MM-DD)

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .targetDate(schedule.getTargetDate().toLocalDate())
                .build();
    }
}
