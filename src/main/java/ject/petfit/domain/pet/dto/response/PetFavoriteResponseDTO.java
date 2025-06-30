package ject.petfit.domain.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetFavoriteResponseDTO {
    private Long petId;
    private Boolean isFavorite;
}
