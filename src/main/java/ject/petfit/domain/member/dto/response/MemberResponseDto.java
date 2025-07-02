package ject.petfit.domain.member.dto.response;


import ject.petfit.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private String nickname;
    private Role role;
}
