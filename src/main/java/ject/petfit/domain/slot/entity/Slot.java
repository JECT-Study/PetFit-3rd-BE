package ject.petfit.domain.slot.entity;

import jakarta.persistence.*;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import lombok.*;
import ject.petfit.domain.pet.entity.Pet;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "slot")
@Builder
@AllArgsConstructor
@ToString
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @Column(nullable = false)
    private boolean feedActivated;     // 사료 슬롯 활성화 여부
    private Integer feedAmount;        // 사료 목표량

    @Column(nullable = false)
    private boolean waterActivated;    // 음수 슬롯 활성화 여부
    private Integer waterAmount;       // 음수 목표량

    @Column(nullable = false)
    private boolean walkActivated;     // 산책 슬롯 활성화 여부
    private Integer walkAmount;        // 산책 목표량

    @Column(nullable = false)
    private boolean pottyActivated;    // 배변 슬롯 활성화 여부

    private Boolean supplementActivated;    // 영양제 슬롯 활성화 여부
    private Integer supplementAmount;       // 영양제 목표량

    private Boolean medicineActivated;      // 약 슬롯 활성화 여부
    private Integer medicineAmount;         // 약 목표량

    private Boolean custom1Activated;       // 커스텀1 슬롯 활성화 여부
    private String custom1Name;             // 커스텀1 이름
    private String custom1Content;          // 커스텀1 내용

    private Boolean custom2Activated;       // 커스텀2 슬롯 활성화 여부
    private String custom2Name;             // 커스텀2 이름
    private String custom2Content;          // 커스텀2 내용

    private Boolean custom3Activated;       // 커스텀3 슬롯 활성화 여부
    private String custom3Name;             // 커스텀3 이름
    private String custom3Content;          // 커스텀3 내용

    @OneToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public void updateFeedActivated(boolean activated) {
        this.feedActivated = activated;
    }
    public void updateWaterActivated(boolean activated) {
        this.waterActivated = activated;
    }
    public void updateWalkActivated(boolean activated) {
        this.walkActivated = activated;
    }
    public void updatePottyActivated(boolean activated) {
        this.pottyActivated = activated;
    }
    public void updateFeedAmount(Integer amount) {
        this.feedAmount = amount;
    }
    public void updateWaterAmount(Integer amount) {
        this.waterAmount = amount;
    }
    public void updateWalkAmount(Integer amount) {
        this.walkAmount = amount;
    }

    // ToDo: deprecated
    @Column(nullable = false)
    private boolean dentalActivated;   // 치아 슬롯 활성화 여부
    @Column(nullable = false)
    private boolean skinActivated;     // 피부 슬롯 활성화 여부
    public void updateDentalActivated(boolean activated) {
        this.dentalActivated = activated;
    }
    public void updateSkinActivated(boolean activated) {
        this.skinActivated = activated;
    }


    public void updateSlot(SlotRequest request) {
        // 활성화 여부
        if (request.getFeedActivated() != null) {
            this.feedActivated = request.getFeedActivated();
        }
        if (request.getWaterActivated() != null) {
            this.waterActivated = request.getWaterActivated();
        }
        if (request.getWalkActivated() != null) {
            this.walkActivated = request.getWalkActivated();
        }
        if (request.getPottyActivated() != null) {
            this.pottyActivated = request.getPottyActivated();
        }
        if (request.getSupplementActivated() != null) {
            this.supplementActivated = request.getSupplementActivated();
        }
        if (request.getMedicineActivated() != null) {
            this.medicineActivated = request.getMedicineActivated();
        }

        // ToDo: deprecated
        if (request.getDentalActivated() != null) {
            this.dentalActivated = request.getDentalActivated();
        }
        if (request.getSkinActivated() != null) {
            this.skinActivated = request.getSkinActivated();
        }

        // 목표량
        if (request.getFeedAmount() != null) {
            this.feedAmount = request.getFeedAmount();
        }
        if (request.getWaterAmount() != null) {
            this.waterAmount = request.getWaterAmount();
        }
        if (request.getWalkAmount() != null) {
            this.walkAmount = request.getWalkAmount();
        }

        // 목표 횟수
        if (request.getSupplementAmount() != null) {
            this.supplementAmount = request.getSupplementAmount();
        }
        if (request.getMedicineAmount() != null) {
            this.medicineAmount = request.getMedicineAmount();
        }

        // 커스텀 슬롯
        if (request.getCustom1Activated() != null) {
            this.custom1Activated = request.getCustom1Activated();
        }
        if (request.getCustom1Name() != null) {
            this.custom1Name = request.getCustom1Name();
        }
        if (request.getCustom1Content() != null) {
            this.custom1Content = request.getCustom1Content();
        }
        if (request.getCustom2Activated() != null) {
            this.custom2Activated = request.getCustom2Activated();
        }
        if (request.getCustom2Name() != null) {
            this.custom2Name = request.getCustom2Name();
        }
        if (request.getCustom2Content() != null) {
            this.custom2Content = request.getCustom2Content();
        }
        if (request.getCustom3Activated() != null) {
            this.custom3Activated = request.getCustom3Activated();
        }
        if (request.getCustom3Name() != null) {
            this.custom3Name = request.getCustom3Name();
        }
        if (request.getCustom3Content() != null) {
            this.custom3Content = request.getCustom3Content();
        }
    }
}
