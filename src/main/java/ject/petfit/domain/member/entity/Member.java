package ject.petfit.domain.member.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import ject.petfit.domain.note.entity.Note;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="authuser_id")
    private AuthUser authUser;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> note = new ArrayList<>();

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Chat> chattings;

    @Builder
    public Member(String nickname, AuthUser authUser, Role role) {
        this.nickname = nickname;
        this.authUser = authUser;
        this.role = role;
    }

    public void addAuthUser(AuthUser authUser) {
        this.authUser = authUser;
        authUser.addMember(this); // 양방향 관계 설정
    }

    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setMember(this); // 양방향 관계 설정
    }

    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setMember(this);
    }

    public void addNote(Note note) {
        this.note.add(note);
        note.setMember(this);
    }


}
