package ject.petfit.domain.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.slot.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "6개 모든 루틴 응답 DTO")
public class DailyAllRoutineResponse {
    private RoutineResponse feedRoutine;
    private RoutineResponse waterRoutine;
    private RoutineResponse walkRoutine;

    private RoutineResponse pottyRoutine;
    private RoutineResponse dentalRoutine;
    private RoutineResponse skinRoutine;

}
