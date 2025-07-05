package ject.petfit.domain.slot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SlotRequest {
    @Schema(description = "사료 슬롯 활성화 여부", example = "true")
    private Boolean feedActivated;      // 사료 슬롯 활성화 여부

    @Schema(description = "음수 슬롯 활성화 여부", example = "true")
    private Boolean waterActivated;     // 음수 슬롯 활성화 여부

    @Schema(description = "산책 슬롯 활성화 여부", example = "false")
    private Boolean walkActivated;      // 산책 슬롯 활성화 여부

    @Schema(description = "배변 슬롯 활성화 여부", example = "true")
    private Boolean pottyActivated;     // 배변 슬롯 활성화 여부

    @Schema(description = "치아 슬롯 활성화 여부", example = "false")
    private Boolean dentalActivated;    // 치아 슬롯 활성화 여부

    @Schema(description = "피부 슬롯 활성화 여부", example = "false")
    private Boolean skinActivated;      // 피부 슬롯 활성화 여부

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
