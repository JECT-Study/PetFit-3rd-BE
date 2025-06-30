package ject.petfit.domain.slot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ject.petfit.domain.slot.entity.Slot;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "슬롯 목표량 응답 DTO")
public class SlotAmountResponse {
    private Integer feedAmount; // 사료 목표량
    private Integer waterAmount; // 음수 목표량
    private Integer walkAmount; // 산책 목표량

    public static SlotAmountResponse from(Slot slot) {
        return SlotAmountResponse.builder()
                .feedAmount(slot.getFeedAmount())
                .waterAmount(slot.getWaterAmount())
                .walkAmount(slot.getWalkAmount())
                .build();
    }
}
