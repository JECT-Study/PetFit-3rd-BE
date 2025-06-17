package ject.petfit.domain.slot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        name = "slot",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"member_id", "pet_id", "category_id"}
                )
        }
)
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "value")
    private int value;

    @Column(name = "is_checked")
    private boolean isChecked;

    @Column(name = "is_significant")
    private boolean isSignificant;

    @Column(name = "significant_value")
    private String significantValue;

    @Builder
    public Slot(Member member, Pet pet, Category category, int value, boolean isChecked, boolean isSignificant, String significantValue) {
        this.member = member;
        this.pet = pet;
        this.category = category;
        this.value = value;
        this.isChecked = isChecked;
        this.isSignificant = isSignificant;
        this.significantValue = significantValue;
    }
}
