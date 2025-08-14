package ject.petfit.domain.routine.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.exception.RoutineErrorCode;
import ject.petfit.domain.routine.exception.RoutineException;
import ject.petfit.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineQueryService {
    private final RoutineRepository routineRepository;

    public List<Routine> getDailyRoutinesOrEmptyList(Entry entry) {
        if (entry == null) {
            return new ArrayList<>();
        }
        return routineRepository.findAllByEntry(entry);
    }

    public Routine getRoutineOrThrow(Entry entry, String category) {
        return routineRepository.findByEntryAndCategory(entry, category)
                .orElseThrow(() -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));
    }
}
