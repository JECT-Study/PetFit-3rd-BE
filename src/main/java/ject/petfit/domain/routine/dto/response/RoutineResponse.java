package ject.petfit.domain.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "루틴 응답 DTO")
public class RoutineResponse {
    @Schema(description = "루틴 ID", example = "1")
    private Long routineId; // 루틴 ID
    @Schema(description = "루틴 카테고리", example = "feed")
    private String category; // 루틴 카테고리
    @Schema(description = "루틴 상태 (체크 완료, 세모 등)", example = "CHECKED")
    private RoutineStatus status; // 루틴 상태 (체크 완료, 세모 등)
    @Schema(description = "목표량", example = "150")
    private Integer targetAmount; // 목표량
    @Schema(description = "실제량", example = "150")
    private Integer actualAmount; // 실제량

    private String content; // 내용 or 메모
    private String date; // 날짜 (YYYY-MM-DD)

    public static RoutineResponse from(Routine routine) {
        return RoutineResponse.builder()
                .routineId(routine.getRoutineId())
                .category(routine.getCategory())
                .status(routine.getStatus())
                .targetAmount(routine.getTargetAmount())
                .actualAmount(routine.getActualAmount())
                .content(routine.getContent())
                .date(routine.getEntry().getEntryDate().toString())
                .build();
    }
}
