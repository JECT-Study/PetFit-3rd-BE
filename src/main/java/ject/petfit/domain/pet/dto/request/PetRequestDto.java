package ject.petfit.domain.pet.dto.request;

import java.time.LocalDate;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.pet.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetRequestDto {
    private String name;
    private String type;
    private String gender;
    private LocalDate birthDate;
    private Boolean isFavorite;
    private Long authUserId;

}
