package ject.petfit.domain.remark.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemarkRegisterRequest {
    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    private String title;

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    private String content;

    @NotNull(message = "날짜는 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 YYYY-MM-DD 형식이어야 합니다.")
    private String remarkDate;
}

