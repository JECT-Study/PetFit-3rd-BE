package ject.petfit.domain.routine.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import ject.petfit.domain.routine.dto.request.RoutineUpdateRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PastRoutineService {
    @Transactional
    public RoutineResponse updateRoutine(Long routineId, @Valid RoutineUpdateRequest routineUpdateRequest) {
        return null;
    }
}
