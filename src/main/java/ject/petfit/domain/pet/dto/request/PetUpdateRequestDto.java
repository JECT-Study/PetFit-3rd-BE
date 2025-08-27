package ject.petfit.domain.pet.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetUpdateRequestDto {

    private Long memberId;

    @Size(max = 20, message = "내용은 20자 이내여야 합니다.")
    private String name;

    @Pattern(regexp = "^(강아지|고양이|햄스터|조류|파충류|어류)$",
            message = "반려동물 타입은 강아지, 고양이, 햄스터, 조류, 파충류, 어류 중에서 선택해야 합니다.")
    private String type;

    @Pattern(regexp = "^(남아|여아|중성)$", message = "성별은 남아, 여아, 중성 중에서 선택해야 합니다.")
    private String gender;

    private LocalDate birthDate;
}
