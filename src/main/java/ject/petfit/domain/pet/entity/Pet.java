package ject.petfit.domain.pet.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.note.entity.Note;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.slot.entity.Slot;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id", updatable = false)
    private Long id;

    @Column(name = "pet_name")
    private String name;

    @Column(name = "pet_type")
    private String type;

    @Column(name = "pet_gender")
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_first")
    private boolean isFirst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Slot> slots;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> note = new ArrayList<>();


    @Builder
    public Pet(String name, String type, String gender, LocalDate birthDate, boolean isFirst) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isFirst = isFirst;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setPet(this);
    }

    public void addNote(Note note) {
        this.note.add(note);
        note.setPet(this);
    }

}
