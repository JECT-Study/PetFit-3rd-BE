package ject.petfit.domain.slot.entity;

import jakarta.persistence.*;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.global.common.BaseTime;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Table(
        name = "slot_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"slot_id", "pet_id"})
        }
)
public class SlotHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotHistoryId;

    @Column(nullable = false)
    private LocalDate recordDate;       // 기록 날짜

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
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
    public void updateDentalActivated(boolean activated) {
        this.dentalActivated = activated;
    }
    public void updateSkinActivated(boolean activated) {
        this.skinActivated = activated;
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
