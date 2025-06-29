package ject.petfit.domain.slot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SlotAmountRequest {
    @Schema(description = "사료 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer feedAmount; // 사료 목표량

    @Schema(description = "음수 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer waterAmount; // 음수 목표량

    @Schema(description = "산책 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer walkAmount; // 산책 목표량
}
