package ject.petfit.domain.pet.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetResponseDto {

    private Long id;
    private String name;
    private String type;
    private String gender;
    private LocalDate birthDate;
    private Boolean isFirst;
}
