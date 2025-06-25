package ject.petfit.domain.pet.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetRequestDto {
    private String name;
    private String type;
    private String gender;
    private LocalDate birthDate;
    private Boolean isFirst;
}
