package ject.petfit.domain.alarm.entity;

import jakarta.persistence.*;
import ject.petfit.domain.pet.entity.Pet;
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
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;
    private LocalDateTime targetDateTime;
    private String title;
    private String content;
    private boolean isSent = false;           // 알람 전송 여부
    private boolean isRead = false;           // 알람 읽음 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public void markAsSent() {
        this.isSent = true;
    }
    public void markAsRead() {
        this.isRead = true;
    }
}
