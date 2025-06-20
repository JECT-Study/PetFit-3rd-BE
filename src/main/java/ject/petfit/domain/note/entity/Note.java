package ject.petfit.domain.note.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id", updatable = false)
    private Long id;

    @Column(name = "note_title")
    private String title;

    @Column(name = "note_content")
    private String content;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Builder
    public Note(String title, String content, LocalDate createdAt, Pet pet) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.pet = pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
