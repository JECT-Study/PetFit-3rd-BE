package ject.petfit.domain.aireport.dto.response;

import ject.petfit.domain.aireport.entity.AiReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiReportResponseDto {
    private Long aiReportId;
    private String title;
    private String content;
    private Long petId;
    private LocalDate startDate;
    private LocalDate endDate;

    public static AiReportResponseDto from(AiReport aiReport) {
        return AiReportResponseDto.builder()
                .aiReportId(aiReport.getId())
                .title(aiReport.getTitle())
                .content(aiReport.getContent())
                .petId(aiReport.getPet().getId())
                .startDate(aiReport.getStartDate())
                .endDate(aiReport.getEndDate())
                .build();
    }
}
