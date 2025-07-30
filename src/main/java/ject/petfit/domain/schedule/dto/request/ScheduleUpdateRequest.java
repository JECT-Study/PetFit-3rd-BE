package ject.petfit.domain.schedule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleUpdateRequest {
    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    @Schema(description = "일정 제목", example = "병원")
    private String title;        // 일정 제목

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    @Schema(description = "일정 내용", example = "제일 조은 동물병원, 치석 제거")
    private String content;      // 일정 내용

    @NotNull(message = "날짜는 필수입니다.")
    @Schema(description = "일정 날짜 (yyyy-MM-dd 형식)", example = "2025-08-01")
    private LocalDate targetDate;  // 일정 날짜 (yyyy-MM-dd 형식)
}
