package ject.petfit.domain.slot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.slot.entity.Slot;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "슬롯 초기화 응답 DTO")
public class SlotResponse {
    private boolean feedActivated;      // 사료 슬롯 활성화 여부
    private boolean waterActivated;     // 음수 슬롯 활성화 여부
    private boolean walkActivated;      // 산책 슬롯 활성화 여부
    private boolean pottyActivated;     // 배변 슬롯 활성화 여부
    private boolean dentalActivated;    // 치아 슬롯 활성화 여부
    private boolean skinActivated;      // 피부 슬롯 활성화 여부
    private Boolean supplementActivated; // 영양제 슬롯 활성화 여부
    private Boolean medicineActivated;   // 약 슬롯 활성화 여부

    private Integer feedAmount;         // 사료 목표량
    private Integer waterAmount;        // 음수 목표량
    private Integer walkAmount;         // 산책 목표량
    private Integer supplementAmount;   // 영양제 목표량
    private Integer medicineAmount;     // 약 목표량

    private Boolean custom1Activated;   // 커스텀1 슬롯 활성화 여부
    private String custom1Name;         // 커스텀1 이름
    private String custom1Content;      // 커스텀1 내용
    private Boolean custom2Activated;   // 커스텀2 슬롯 활성화 여부
    private String custom2Name;         // 커스텀2 이름
    private String custom2Content;      // 커스텀2 내용
    private Boolean custom3Activated;   // 커스텀3 슬롯 활성화 여부
    private String custom3Name;         // 커스텀3 이름
    private String custom3Content;      // 커스텀3 내용

    public static SlotResponse from(Slot slot){
        return SlotResponse.builder()
                .feedActivated(slot.isFeedActivated())
                .waterActivated(slot.isWaterActivated())
                .walkActivated(slot.isWalkActivated())
                .pottyActivated(slot.isPottyActivated())
                .dentalActivated(slot.isDentalActivated())
                .skinActivated(slot.isSkinActivated())
                .supplementActivated(slot.getSupplementActivated())
                .medicineActivated(slot.getMedicineActivated())
                .feedAmount(slot.getFeedAmount())
                .waterAmount(slot.getWaterAmount())
                .walkAmount(slot.getWalkAmount())
                    // 새로 추가
                    .supplementAmount(slot.getSupplementAmount())
                    .medicineAmount(slot.getMedicineAmount())
                    .custom1Activated(slot.getCustom1Activated())
                    .custom1Name(slot.getCustom1Name())
                    .custom1Content(slot.getCustom1Content())
                    .custom2Activated(slot.getCustom2Activated())
                    .custom2Name(slot.getCustom2Name())
                    .custom2Content(slot.getCustom2Content())
                    .custom3Activated(slot.getCustom3Activated())
                    .custom3Name(slot.getCustom3Name())
                    .custom3Content(slot.getCustom3Content())
                .build();
    }
}
