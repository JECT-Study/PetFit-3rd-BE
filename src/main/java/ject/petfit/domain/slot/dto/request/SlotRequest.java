package ject.petfit.domain.slot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SlotRequest {
    /**
     * 활성화 여부
     */
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

    @Schema(description = "영양제 슬롯 활성화 여부", example = "true")
    private Boolean supplementActivated; // 영양제 슬롯 활성화 여부

    @Schema(description = "약 슬롯 활성화 여부", example = "false")
    private Boolean medicineActivated;   // 약 슬롯 활성화 여부

    /**
     * 목표량
     */
    @Schema(description = "사료 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer feedAmount; // 사료 목표량

    @Schema(description = "음수 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer waterAmount; // 음수 목표량

    @Schema(description = "산책 목표량", example = "150")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer walkAmount; // 산책 목표량

    @Schema(description = "영양제 목표량", example = "5")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer supplementAmount; // 영양제 목표량

    @Schema(description = "약 목표 횟수", example = "5")
    @Min(value = 0, message = "목표량은 0 이상이어야 합니다.")
    private Integer medicineAmount; // 약 목표량

    /**
     * 커스텀 슬롯
     */
    @Schema(description = "커스텀1 슬롯 활성화 여부", example = "true")
    private Boolean custom1Activated;   // 커스텀1 슬롯 활성화 여부
    @Schema(description = "커스텀1 이름", example = "커스텀1")
    private String custom1Name;         // 커스텀1 이름
    @Schema(description = "커스텀1 내용", example = "내용1")
    private String custom1Content;      // 커스텀1 내용

    @Schema(description = "커스텀2 슬롯 활성화 여부", example = "false")
    private Boolean custom2Activated;   // 커스텀2 슬롯 활성화 여부
    @Schema(description = "커스텀2 이름", example = "커스텀2")
    private String custom2Name;         // 커스텀2 이름
    @Schema(description = "커스텀2 내용", example = "내용2")
    private String custom2Content;      // 커스텀2 내용

    @Schema(description = "커스텀3 슬롯 활성화 여부", example = "false")
    private Boolean custom3Activated;   // 커스텀3 슬롯 활성화 여부
    @Schema(description = "커스텀3 이름", example = "커스텀3")
    private String custom3Name;         // 커스텀3 이름
    @Schema(description = "커스텀3 내용", example = "내용3")
    private String custom3Content;      // 커스텀3 내용
}

