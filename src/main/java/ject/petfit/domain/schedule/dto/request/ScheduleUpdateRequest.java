package ject.petfit.domain.schedule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
