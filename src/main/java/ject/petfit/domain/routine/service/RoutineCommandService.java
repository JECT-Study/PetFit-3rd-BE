package ject.petfit.domain.routine.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.service.SlotQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class RoutineCommandService {
    private final RoutineRepository routineRepository;
    private final SlotQueryService slotQueryService;


    public Routine getOrCreateRoutine(Entry entry, String category, Slot slot) {
        return routineRepository.findByEntryAndCategory(entry, category)
                .orElseGet(() -> createRoutine(entry, category, slot));
    }

    public Routine createRoutine(Entry entry, String category, Slot slot) {
        return routineRepository.save(Routine.builder()
                .entry(entry)
                .category(category)
                .status(RoutineStatus.UNCHECKED)
                .targetAmount(slotQueryService.getTargetAmountOrNull(slot, category))
                .build());
    }

    public Routine updateRoutine(Routine routine, RoutineStatus routineStatus ,Integer actualAmount, String content) {
        routine.updateStatus(routineStatus);
        routine.updateActualAmount(actualAmount);
        routine.updateContent(content);
        return routine;
    }

    public void deleteRoutine(Routine routine) {
        routineRepository.delete(routine);
    }
}
