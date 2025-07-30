package ject.petfit.global.dev.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.routine.service.RoutineService;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.service.SlotService;
import ject.petfit.global.dev.dto.EntryFlushResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DevService {
    private final PetRepository petRepository;
    private final EntryRepository entryRepository;
    private final RoutineRepository routineRepository;
    private final SlotService slotService;
    private final RoutineService routineService;

    @Transactional
    public void entryDateFlush(Long petId, LocalDate entryDate) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Slot slot = pet.getSlot();
        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
                .orElse(null); // 해당 날짜의 entry가 없다면 빈 리스트 반환
        /**
         * DB에 저장된 CHECKED/MEMO 루틴이 없다면(=Entry가 없다) 할 일 없음
         */
        if(entry == null){
            return;
        }

        // 1. DB에 저장된 CHECKED, MEMO 루틴 리스트를 조회
        List<Routine> routineListInDB = new ArrayList<>(routineRepository.findAllByEntry(entry));

        // 2. 현재 시점 활성화된 슬롯 옵션에서 루틴 완료 여부 판단
        // 활성화된 슬롯 옵션명 조회
        List<String> activatedSlotOptions = slotService.getActivatedSlotOptions(slot);

        /* 슬롯 활성화해놓고 CHECKED나 MEMO한 루틴이지만 밤 12시 시점에 비활성화된 슬롯이면 삭제함 */
        // 에러를 아직 못잡음
//        log.info("삭제 로직 전");
//        for(Routine routine : routineListInDB){
//            if (!activatedSlotOptions.contains(routine.getCategory())) {
//                routineRepository.delete(routine);
//                routineListInDB.remove(routine);
//            }
//        }
//        routineRepository.flush();
//        log.info("삭제 로직 후");

        /**
         * DB에 저장된 루틴 리스트가 활성화된 옵션 개수와 같다면 '루틴 완료' 업데이트하고 종료
         */
        if(routineListInDB.size() == activatedSlotOptions.size()){
            entry.updateCompletedTrue(); // 루틴 완료 업데이트
            return;
        }

        /**
         * DB에 저장된 루틴 리스트가 활성화된 옵션 개수보다 적다면 '루틴 미완료' 업데이트 및
         * 미완료 루틴을 UNCHECKED 상태로 DB에 삽입
         */
        // 루틴 미완료 업데이트
        entry.updateCompletedFalse();

        // 활성화된 옵션 리스트에서 UNCHECKED 루틴들만 남겨놓음
        for(Routine routine : routineListInDB){
            if (activatedSlotOptions.contains(routine.getCategory())) {
                activatedSlotOptions.remove(routine.getCategory());
            }
        }

        // 남은 활성화된 옵션들로 UNCHECKED 루틴 DB 추가
        for(String category : activatedSlotOptions){
            Integer targetAmount = routineService.getTargetAmountByCategory(pet.getSlot(), category);
            routineRepository.save(
                    Routine.builder()
                            .entry(entry)
                            .category(category)
                            .status(RoutineStatus.UNCHECKED)
                            .targetAmount(targetAmount)
                            .actualAmount(0)
                            .build()
            );
        }

    }

    public EntryFlushResponse getEntryFlushResponse(Long petId, LocalDate entryDate) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
                .orElse(null);
        Boolean routineIsCompleted = true;
        if (entry == null) {
            routineIsCompleted = false;
        }

        List<RoutineResponse> routineResponseList = entryDate.equals(LocalDate.now()) ?
                routineService.getTodayRoutines(petId, entryDate) :
                routineService.getPastRoutines(petId, entryDate);

        return EntryFlushResponse.builder()
                .routineIsCompleted(routineIsCompleted)
                .routineResponseList(routineResponseList)
                .build();
    }
}
