package ject.petfit.domain.entry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.entry.entity.Entry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "달력에서 기록 존재 여부 응답 DTO")
public class EntryExistsResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "날짜", example = "2025-07-01")
    private LocalDate entryDate; // 날짜

    @Schema(description = "루틴체크 존재 여부", example = "true")
    private boolean isChecked; // 루틴체크 존재 여부

    @Schema(description = "메모 존재 여부", example = "true")
    private boolean isMemo; // 메모 존재 여부

    @Schema(description = "특이사항 존재 여부", example = "true")
    private boolean isRemarked; // 특이사항 존재 여부

    @Schema(description = "일정 존재 여부", example = "false")
    private boolean isScheduled; // 일정 존재 여부

    public static EntryExistsResponse from(Entry entry) {
        return EntryExistsResponse.builder()
                .entryDate(entry.getEntryDate())
                .isChecked(entry.getIsChecked())
                .isMemo(entry.getIsMemo())
                .isRemarked(entry.getIsRemarked())
                .isScheduled(entry.getIsScheduled())
                .build();
    }
}
