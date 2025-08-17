package ject.petfit.domain.slot.facade;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.service.RoutineCommandService;
import ject.petfit.domain.routine.service.RoutineQueryService;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.service.SlotCommandService;
import ject.petfit.domain.slot.service.SlotQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SlotFacade {
    private final PetQueryService petQueryService;
    private final EntryQueryService entryQueryService;
    private final SlotQueryService slotQueryService;
    private final SlotCommandService slotCommandService;
    private final RoutineQueryService routineQueryService;
    private final RoutineCommandService routineCommandService;

    // 슬롯 초기화 (회원가입 슬롯 설정)
    @Transactional
    public SlotResponse initializePetSlot(Long petId, SlotInitializeRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        slotQueryService.validateSlotExists(pet); // 슬롯 레코드가 이미 존재하는지 검증
        return SlotResponse.from(slotCommandService.createSlot(pet, request));
    }

    // 슬롯 활성화 상태 조회
    public SlotResponse getSlotActivated(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        return SlotResponse.from(slotQueryService.getSlotOrThrow(pet));
    }

    // 슬롯 활성화 상태 변경
    @Transactional
    public SlotResponse setSlotActivated(Long petId, SlotRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        Slot slot = slotQueryService.getSlotOrThrow(pet);

        // 슬롯 옵션 업데이트 및 비활성화 카테고리들 반환
        List<String> deActivatedList = slotCommandService.updateSlot(slot, request);

        /**
         * 비활성화된 슬롯이 존재하면서,
         * 이전에 활성화 -> 비활성화로 변경한 CHECKED나 MEMO한 오늘의 루틴이 DB에 있다면 삭제
         */
        // 비활성화된 슬롯이 없다면 종료
        if (deActivatedList.isEmpty()) {
            return SlotResponse.from(slot);
        }

        // 오늘의 entry 조회
        Optional<Entry> entry = entryQueryService.getEntryOptional(pet, LocalDate.now());

        // 오늘의 entry가 없다면 종료
        if (entry.isEmpty()) {
            return SlotResponse.from(slot);
        }

        // 비활성화된 슬롯에 해당하는 루틴이 DB에 들어있다면 삭제
        for (String category : deActivatedList) {
            Optional<Routine> routine = routineQueryService.getRoutineOptional(entry.get(), category);
            routine.ifPresent(routineCommandService::deleteRoutine);
        }
        return SlotResponse.from(slot);
    }
}
