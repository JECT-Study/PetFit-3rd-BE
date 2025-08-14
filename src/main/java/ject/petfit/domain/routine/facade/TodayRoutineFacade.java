package ject.petfit.domain.routine.facade;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.routine.service.RoutineService;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodayRoutineFacade {
    private final RoutineRepository routineRepository;
    private final SlotService slotService;
    private final RoutineService routineService;

    /**
     * 오늘의 루틴 자동 업데이트 (batch schedule)
     * 1. 오늘 날짜의 Entry를 모두 조회 (모든 Pet 대상)
     * 2. CHECKED나 MEMO한 루틴이 없다면 루틴 미완료로 저장 및 3, 4번 미실행
     * 3. 오늘의 루틴 완료 여부 저장
     * 4. UNCHECKED 상태의 루틴들을 DB에 저장
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean todayRoutineSave(Entry entry){
        /** 2. CHECKED나 MEMO한 루틴이 없다면 루틴 미완료로 저장 및 종료 */
        if (!routineRepository.existsByEntryAndStatus(entry, RoutineStatus.CHECKED)
                && !routineRepository.existsByEntryAndStatus(entry, RoutineStatus.MEMO)) {
            entry.updateCompletedFalse(); // 루틴 미완료 업데이트
            return false; // 3,4번 미실행
        }

        /**
         * 3. DB에 저장된 오늘의 루틴 개수와 활성화된 옵션 개수가 같다면 루틴 완료로 업데이트하고 종료
         *    DB에 저장된 루틴 리스트가 활성화된 옵션 개수보다 적다면 루틴 미완료로 업데이트
         */
        // DB에 저장된 CHECKED, MEMO 루틴 리스트를 조회
        List<Routine> routineListInDB = new ArrayList<>(routineRepository.findAllByEntry(entry));

        // 활성화된 슬롯 옵션 조회
        Slot slot = entry.getPet().getSlot();
        List<String> activatedSlotOptions = slotService.getActivatedSlotCategories(slot);

        if (routineListInDB.size() == activatedSlotOptions.size()) {
            entry.updateCompletedTrue(); // 루틴 완료 업데이트
            return true;
        }
        entry.updateCompletedFalse(); // 루틴 미완료 업데이트

        /**
         * 4. UNCHECKED 상태의 루틴들을 DB에 저장
         */
        // 활성화된 옵션 리스트에서 UNCHECKED 루틴들만 남겨놓음
        for (Routine routine : routineListInDB) {
            if (activatedSlotOptions.contains(routine.getCategory())) {
                activatedSlotOptions.remove(routine.getCategory());
            }
        }

        // 남은 활성화된 옵션들로 UNCHECKED 루틴 DB 추가
        for (String category : activatedSlotOptions) {
            Integer targetAmount = slotService.getTargetAmountOrNull(slot, category);
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
        return false;

    }
}
