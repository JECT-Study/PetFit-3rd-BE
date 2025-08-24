package ject.petfit.domain.routine.facade;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.entry.service.EntryCommandService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.routine.dto.request.RoutineMemoRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.service.RoutineCommandService;
import ject.petfit.domain.routine.service.RoutineQueryService;
import ject.petfit.domain.slot.service.SlotQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoutineFacade {

    private final PetQueryService petQueryService;
    private final RoutineQueryService routineQueryService;
    private final EntryCommandService entryCommandService;
    private final EntryQueryService entryQueryService;
    private final SlotQueryService slotQueryService;
    private final RoutineCommandService routineCommandService;

    public List<RoutineResponse> getDailyRoutines(Long petId, LocalDate date) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        Optional<Entry> entry = entryQueryService.getEntryOptional(pet, date);
        return routineQueryService.getDailyRoutines(pet, entry, date);
    }

    // 루틴 체크(V)
    @Transactional
    public String checkRoutine(Long petId, LocalDate entryDate, String category) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        // 활성화된 슬롯의 요청인지 검증
        slotQueryService.validateSlotCategoryActivated(pet.getSlot(), category);
        // 해당 날짜의 entry가 있으면 조회 없으면 생성
        Entry entry = entryCommandService.getOrCreateEntry(pet, entryDate);

        // (날짜, 카테고리) 루틴 조회해서 없으면 생성 있으면 수정(메모->체크 수정하는 케이스, 메모 중복 체크도 포함)
        Routine routine = routineCommandService.getOrCreateRoutine(entry, category, pet.getSlot());

        // 카테고리 따라 목표량 달라짐
        Integer targetAmount = slotQueryService.getTargetAmountOrNull(pet.getSlot(), category);

        routineCommandService.updateRoutine(
                routine,
                RoutineStatus.CHECKED, // 상태를 체크로 변경
                targetAmount, // 목표량으로 실제량 설정
                null // 내용은 null로 설정 (메모가 없으므로)
        );

        return "CHECKED";
    }

    // 루틴 세모
    @Transactional
    public RoutineResponse addMemoRoutine(Long petId, LocalDate entryDate, String category, RoutineMemoRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        // 활성화된 슬롯의 요청인지 검증
        slotQueryService.validateSlotCategoryActivated(pet.getSlot(), category);
        // 해당 날짜의 entry가 있으면 조회 없으면 생성
        Entry entry = entryCommandService.getOrCreateEntry(pet, entryDate);

        // (날짜, 카테고리) 루틴 조회해서 없으면 생성 있으면 수정(체크->메모 수정하는 케이스)
        Routine routine = routineCommandService.getOrCreateRoutine(entry, category, pet.getSlot());

        Routine updatedRoutine = routineCommandService.updateRoutine(
                routine,
                RoutineStatus.MEMO, // 상태를 메모로 변경
                request.getActualAmount(),
                request.getContent()
        );

        return RoutineResponse.from(updatedRoutine);
    }

    // 루틴 해제
    @Transactional
    public String uncheckRoutine(Long petId, LocalDate entryDate, String category) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        // 활성화된 슬롯의 요청인지 확인
        slotQueryService.validateSlotCategoryActivated(pet.getSlot(), category);

        // 해당 날짜의 entry가 없으면 예외 발생
        Entry entry = entryQueryService.getEntryOrThrow(pet, entryDate);

        // 루틴 조회 및 삭제
        Routine routine = routineQueryService.getRoutineOrThrow(entry, category);
        routineCommandService.deleteRoutine(routine);

        return "UNCHECKED";
    }
}
