package ject.petfit.domain.remark.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemarkUpdateRequest {
    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    private String title;

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    private String content;
}

