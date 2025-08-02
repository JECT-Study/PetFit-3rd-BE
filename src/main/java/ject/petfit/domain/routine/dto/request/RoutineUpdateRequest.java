package ject.petfit.domain.routine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RoutineUpdateRequest {
    @Min(value = 0, message = "분량은 0 이상이어야 합니다.")
    @Max(value = 99999, message = "분량은 5자리 이하여야 합니다.")
    @Schema(description = "실제량(사료, 음수, 산책)", example = "0")
    private Integer actualAmount;   // 실제량 (사료, 음수, 산책에만 응답)

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    @Schema(description = "내용 or 메모", example = "비와서 산책 안 감")
    private String content;         // 내용 or 메모
}
