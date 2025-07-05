package ject.petfit.domain.member.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {
    @Size(max = 10, message = "내용은 10자 이내여야 합니다.")
    private String nickname;
}
