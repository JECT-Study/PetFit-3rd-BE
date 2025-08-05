package ject.petfit.domain.pet.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetListResponseDto {
    private Long id;
    private String name;
    private String type;
    private Boolean isFavorite;
}
