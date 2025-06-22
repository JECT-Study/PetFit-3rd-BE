package ject.petfit.domain.remark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.remark.entity.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "특이사항 응답 DTO")
public class RemarkResponse {
    @Schema(description = "특이사항 ID", example = "1")
    private Long scheduleId; // 일정 ID

    @Schema(description = "특이사항 제목", example = "댕댕이 구토")
    private String title; // 일정 제목

    @Schema(description = "특이사항 내용", example = "댕댕이가 오늘 열이나고..")
    private String content; // 일정 내용

    @Schema(description = "대상 날짜 (YYYY-MM-DD)", example = "2025-07-01")
    private String remarkDate; // 대상 날짜 (YYYY-MM-DD)

    public static RemarkResponse from(Remark remark) {
        return RemarkResponse.builder()
                .scheduleId(remark.getRemarkId())
                .title(remark.getTitle())
                .content(remark.getContent())
                .remarkDate(remark.getRemarkDate().toLocalDate().toString())
                .build();
    }
}
