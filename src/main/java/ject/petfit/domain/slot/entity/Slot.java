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

    @Column(nullable = false)
    private boolean waterActivated;    // 음수 슬롯 활성화 여부

    @Column(nullable = false)
    private boolean walkActivated;     // 산책 슬롯 활성화 여부

    @Column(nullable = false)
    private boolean pottyActivated;    // 배변 슬롯 활성화 여부

    @Column(nullable = false)
    private boolean dentalActivated;   // 치아 슬롯 활성화 여부

    @Column(nullable = false)
    private boolean skinActivated;     // 피부 슬롯 활성화 여부

    private Integer feedAmount;        // 사료 목표량
    private Integer waterAmount;       // 음수 목표량
    private Integer walkAmount;        // 산책 목표량

    @OneToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public void updateFeedAmount(Integer amount) {
        this.feedAmount = amount;
    }
    public void updateWaterAmount(Integer amount) {
        this.waterAmount = amount;
    }
    public void updateWalkAmount(Integer amount) {
        this.walkAmount = amount;
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
    }
}
