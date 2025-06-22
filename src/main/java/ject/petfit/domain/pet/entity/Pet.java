package ject.petfit.domain.pet.entity;

import jakarta.persistence.*;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries;

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



}
