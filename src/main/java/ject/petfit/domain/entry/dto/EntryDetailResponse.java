package ject.petfit.domain.entry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import ject.petfit.domain.remark.entity.Remark;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "하루 루틴, 특이사항 응답 DTO")
public class EntryDetailResponse {
    private RoutineResponse routineResponse; // 루틴 응답
    private RemarkResponse remarkResponse; // 특이사항 응답

    public static EntryDetailResponse of(Routine routine, Remark remark) {
        return EntryDetailResponse.builder()
                .routineResponse(RoutineResponse.from(routine))
                .remarkResponse(RemarkResponse.from(remark))
                .build();
    }
}
