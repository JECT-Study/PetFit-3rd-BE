package ject.petfit.domain.schedule.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import ject.petfit.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", updatable = false)
    private Long id;

    @Column(name = "schedule_title")
    private String title;

    @Column(name = "schedule_content")
    private String content;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Schedule(String title, String content, LocalDate scheduleDate, Member member) {
        this.title = title;
        this.content = content;
        this.scheduleDate = scheduleDate;
        this.member = member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
