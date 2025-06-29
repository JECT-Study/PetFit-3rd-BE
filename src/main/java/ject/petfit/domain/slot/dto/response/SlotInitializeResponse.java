package ject.petfit.domain.slot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.slot.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "슬롯 초기화 응답 DTO")
public class SlotInitializeResponse {
    private boolean feedActivated;      // 사료 슬롯 활성화 여부
    private boolean waterActivated;     // 음수 슬롯 활성화 여부
    private boolean walkActivated;      // 산책 슬롯 활성화 여부
    private boolean pottyActivated;     // 배변 슬롯 활성화 여부
    private boolean dentalActivated;    // 치아 슬롯 활성화 여부
    private boolean skinActivated;      // 피부 슬롯 활성화 여부
    private Integer feedAmount;         // 사료 목표량
    private Integer waterAmount;        // 음수 목표량
    private Integer walkAmount;         // 산책 목표량

    public static SlotInitializeResponse from(Slot slot){
        return SlotInitializeResponse.builder()
                .feedActivated(slot.isFeedActivated())
                .waterActivated(slot.isWaterActivated())
                .walkActivated(slot.isWalkActivated())
                .pottyActivated(slot.isPottyActivated())
                .dentalActivated(slot.isDentalActivated())
                .skinActivated(slot.isSkinActivated())
                .feedAmount(slot.getFeedAmount())
                .waterAmount(slot.getWaterAmount())
                .walkAmount(slot.getWalkAmount())
                .build();
    }
}
