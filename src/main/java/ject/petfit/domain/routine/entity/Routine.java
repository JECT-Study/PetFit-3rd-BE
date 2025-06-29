package ject.petfit.domain.routine.entity;

import jakarta.persistence.*;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.enums.RoutineStatus;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "routine")
@Builder
@AllArgsConstructor
@ToString
public class Routine  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @Column(nullable = false)
    private String category;        // feed, water, walk | potty, dental, skin

    @Enumerated(EnumType.STRING)
    private RoutineStatus status;    // 체크 선택, 세모 (체크 해제는 엔티티 삭제)
    private Integer targetAmount;   // 목표량
    private Integer actualAmount;   // 실제량
    private String content;         // 내용 or 메모

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    public void updateStatus(RoutineStatus status) {
        this.status = status;
    }

    public void updateActualAmount(Integer actualAmount) {
        this.actualAmount = actualAmount;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
