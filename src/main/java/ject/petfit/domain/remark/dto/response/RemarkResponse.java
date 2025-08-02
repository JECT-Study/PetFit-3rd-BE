package ject.petfit.domain.remark.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.remark.entity.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "특이사항 응답 DTO")
public class RemarkResponse {
    @Schema(description = "특이사항 ID", example = "1")
    private Long remarkId; // 일정 ID
    @Schema(description = "특이사항 제목", example = "구토")
    private String title; // 일정 제목
    @Schema(description = "특이사항 내용", example = "아침에 구토를 2회 했음")
    private String content; // 일정 내용
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "특이사항 날짜 (yyyy-MM-dd)", example = "2025-06-29")
    private LocalDate remarkDate; // 특이사항 대상 날짜 (yyyy-MM-dd)

    public static RemarkResponse from(Remark remark) {
        return RemarkResponse.builder()
                .remarkId(remark.getRemarkId())
                .title(remark.getTitle())
                .content(remark.getContent())
                .remarkDate(remark.getRemarkDate().toLocalDate())
                .build();
    }
}
