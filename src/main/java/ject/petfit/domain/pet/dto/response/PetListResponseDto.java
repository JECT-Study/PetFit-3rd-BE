package ject.petfit.domain.pet.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetListResponseDto {
    private Long id;
    private String name;
    private Boolean isFavorite;
}
