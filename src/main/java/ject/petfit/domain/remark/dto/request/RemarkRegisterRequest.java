package ject.petfit.domain.remark.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemarkRegisterRequest {
    @NotNull(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    @Schema(description = "특이사항 제목", example = "댕댕이 구토")
    private String title;

    @Size(max = 200, message = "내용은 200자 이내여야 합니다.")
    @Schema(description = "특이사항 내용", example = "댕댕이가 오늘 열이나고..")
    private String content;

    @NotNull(message = "날짜는 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 YYYY-MM-DD 형식이어야 합니다.")
    @Schema(description = "대상 날짜 (YYYY-MM-DD)", example = "2025-07-01")
    private String remarkDate;
}

