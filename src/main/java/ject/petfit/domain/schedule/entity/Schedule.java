package ject.petfit.domain.schedule.entity;

import jakarta.persistence.*;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.global.common.BaseTime;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Schedule extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private String title;               // 일정 제목
    private String content;             // 일정 내용
    private LocalDateTime targetDate;   // 대상 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    public void updateTitle(String title) {
        this.title = title;
    }
    public void updateContent(String content) {
        this.content = content;
    }

}
