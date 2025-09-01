package ject.petfit.domain.member.entity;


import jakarta.persistence.*;
import java.util.Arrays;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString(exclude = "pets")
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authuser_id")
    private AuthUser authUser;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

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

    public void editNickname(String nickname) {
        this.nickname = nickname;
    }

}