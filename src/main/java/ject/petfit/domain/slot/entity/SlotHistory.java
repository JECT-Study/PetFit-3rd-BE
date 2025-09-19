package ject.petfit.domain.slot.entity;

import jakarta.persistence.*;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.global.common.BaseTime;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Table(name = "slot_history")
public class SlotHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotHistoryId;

    @Column(nullable = false)
    private LocalDate recordDate;       // 기록 날짜

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
    private Integer medicineAmount;          // 약 목표량

    private Boolean custom1Activated;       // 커스텀1 슬롯 활성화 여부
    private String custom1Name;             // 커스텀1 이름
    private String custom1Content;          // 커스텀1 내용

    private Boolean custom2Activated;      // 커스텀2 슬롯 활성화 여부
    private String custom2Name;            // 커스텀2 이름
    private String custom2Content;         // 커스텀2 내용

    private Boolean custom3Activated;      // 커스텀3 슬롯 활성화 여부
    private String custom3Name;            // 커스텀3 이름
    private String custom3Content;         // 커스텀3 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // ToDo: deprecated
    @Column(nullable = false)
    private boolean dentalActivated;   // 치아 슬롯 활성화 여부
    @Column(nullable = false)
    private boolean skinActivated;     // 피부 슬롯 활성화 여부

}
