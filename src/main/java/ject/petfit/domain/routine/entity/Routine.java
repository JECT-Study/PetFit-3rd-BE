package ject.petfit.domain.routine.entity;

import jakarta.persistence.*;
import ject.petfit.domain.entry.entity.Entry;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "routine")
@Builder
@AllArgsConstructor
@ToString
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @Column(nullable = false)
    private String category;

    private String status;          // 체크 선택, 체크 해제, 세모
    private Integer targetAmount;   // 목표량
    private Integer actualAmount;   // 실제량
    private String content;         // 내용 or 메모

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private Entry entry;
}
