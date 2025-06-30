package ject.petfit.domain.slot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SlotInitializeRequest {
    @Schema(description = "사료 슬롯 활성화 여부", defaultValue = "true")
    private boolean feedActivated;      // 사료 슬롯 활성화 여부

    @Schema(description = "음수 슬롯 활성화 여부", defaultValue = "true")
    private boolean waterActivated;     // 음수 슬롯 활성화 여부

    @Schema(description = "산책 슬롯 활성화 여부", defaultValue = "false")
    private boolean walkActivated;      // 산책 슬롯 활성화 여부

    @Schema(description = "배변 슬롯 활성화 여부", defaultValue = "true")
    private boolean pottyActivated;     // 배변 슬롯 활성화 여부

    @Schema(description = "치아 슬롯 활성화 여부", defaultValue = "false")
    private boolean dentalActivated;    // 치아 슬롯 활성화 여부

    @Schema(description = "피부 슬롯 활성화 여부", defaultValue = "false")
    private boolean skinActivated;      // 피부 슬롯 활성화 여부

    @Schema(description = "사료 목표량", example = "150")
    private Integer feedAmount; // 사료 목표량

    @Schema(description = "음수 목표량", example = "150")
    private Integer waterAmount; // 음수 목표량

    @Schema(description = "산책 목표량", example = "150")
    private Integer walkAmount; // 산책 목표량
}
