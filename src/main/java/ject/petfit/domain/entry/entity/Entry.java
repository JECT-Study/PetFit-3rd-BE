package ject.petfit.domain.entry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.remark.entity.Remark;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Table(name = "entry", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entry_date", "pet_id"})
})
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    private Boolean isChecked;
    private Boolean isMemo;
    private Boolean isRemarked;
    private Boolean isScheduled;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Routine> routines;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Remark> remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public void updateCheckedTrue() {
        this.isChecked = true;
    }
    public void updateMemoTrue() {
        this.isMemo = true;
    }
    public void updateRemarkedTrue() {
        this.isRemarked = true;
    }
    public void updateScheduledTrue() {
        this.isScheduled = true;
    }

    public void updateScheduledFalse() {
        this.isScheduled = false;
    }

    public void updateCheckedFalse() {
        this.isChecked = false;
    }
    public void updateMemoFalse() {
        this.isMemo = false;
    }
    public void updateRemarkedFalse() {
        this.isRemarked = false;
    }
}