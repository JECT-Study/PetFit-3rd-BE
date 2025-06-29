package ject.petfit.domain.pet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetFavoriteRequestDTO {
    private Long petId;
    private Boolean isFavorite;
}
