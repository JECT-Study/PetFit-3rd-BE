package ject.petfit.global.dev;

import io.swagger.v3.oas.annotations.media.Schema;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
@Schema(description = "루틴 완료 여부와 해당 날짜의 루틴 리스트를 응답")
public class EntryFlushResponse {
    private boolean routineIsCompleted;
    List<RoutineResponse> routineResponseList;
}
