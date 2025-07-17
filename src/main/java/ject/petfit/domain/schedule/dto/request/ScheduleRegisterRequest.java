package ject.petfit.domain.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ScheduleRegisterRequest {
    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    @Schema(description = "일정 제목", example = "병원")
    private String title;        // 일정 제목

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    @Schema(description = "일정 내용", example = "제일 조은 동물병원, 치석 제거")
    private String content;      // 일정 내용

    @NotNull(message = "날짜는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "대상 날짜 (yyyy-MM-dd)", example = "2025-07-01")
    private LocalDate targetDate;   // 대상 날짜(yyyy-MM-dd)
}
