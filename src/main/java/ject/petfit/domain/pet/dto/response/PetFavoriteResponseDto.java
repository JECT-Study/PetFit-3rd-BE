package ject.petfit.domain.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetFavoriteResponseDto {
    private Long petId;
    private Boolean isFavorite;
}
