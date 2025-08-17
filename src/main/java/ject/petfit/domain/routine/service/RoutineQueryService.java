package ject.petfit.domain.routine.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.exception.RoutineErrorCode;
import ject.petfit.domain.routine.exception.RoutineException;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.service.SlotQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutineQueryService {
    private final RoutineRepository routineRepository;
    private final SlotQueryService slotQueryService;

    public List<Routine> getDailyRoutinesOrEmptyList(Optional<Entry> entry) {
        if (entry.isEmpty()) {
            return new ArrayList<>();
        }
        return routineRepository.findAllByEntry(entry.get());
    }

    public Routine getRoutineOrThrow(Entry entry, String category) {
        return routineRepository.findByEntryAndCategory(entry, category)
                .orElseThrow(() -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));
    }

    public Optional<Routine> getRoutineOptional(Entry entry, String category) {
        return routineRepository.findByEntryAndCategory(entry, category);
    }

    // 루틴 응답 DTO 생성
    private RoutineResponse createRoutineResponse(Routine routine) {
        return RoutineResponse.builder()
                .routineId(routine.getRoutineId())
                .category(routine.getCategory())
                .status(routine.getStatus())
                .targetAmount(routine.getTargetAmount())
                .actualAmount(routine.getActualAmount())
                .content(routine.getContent())
                .date(routine.getEntry().getEntryDate())
                .build();
    }

    // 오늘의 루틴 UNCHECKED 응답 DTO 생성
    private RoutineResponse uncheckedRoutineResponse(LocalDate todayDate, String category, Integer targetAmount) {
        return RoutineResponse.builder()
                .routineId(null)
                .category(category)
                .status(RoutineStatus.UNCHECKED)
                .targetAmount(targetAmount)
                .actualAmount(0)
                .content(null)
                .date(todayDate)
                .build();
    }

    // 과거의 루틴 조회
    public List<RoutineResponse> getPastRoutines(Optional<Entry> entry) {
        return new ArrayList<>(getDailyRoutinesOrEmptyList(entry).stream()
                .map(this::createRoutineResponse)
                .toList());
    }


    // 오늘의 루틴 조회
    public List<RoutineResponse> getTodayRoutines(Optional<Entry> entry, Slot slot) {
        /**
         * 오늘의 루틴 응답 리스트 생성 방법
         * 1. DB에 저장된 CHECKED, MEMO 루틴을 응답 리스트 DTO에 추가
         * 2. 슬롯 활성화된 옵션 중에 DB에 없는 루틴을 UNCHECKED로 간주하여 응답 DTO에 추가
         */
        // 루틴 응답 리스트
        List<RoutineResponse> routineResponseList = new ArrayList<>();

        // 1. DB에 저장된 CHECKED, MEMO 루틴 리스트를 추가
        List<Routine> routineListInDB = new ArrayList<>();
        routineListInDB.addAll(getDailyRoutinesOrEmptyList(entry));
        routineResponseList.addAll(routineListInDB.stream()
                .map(this::createRoutineResponse)
                .toList());

        // 2. 현재 활성화된 슬롯 옵션 중에 UNCHECKED인 루틴 리스트를 추가
        // 활성화된 슬롯 카테고리 조회
        List<String> activatedSlotList = slotQueryService.getActivatedSlotCategories(slot);

        // DB에 저장된 루틴이 활성화된 슬롯 옵션 개수와 같으면(루틴 완료) 이대로 반환
        if(routineResponseList.size() == activatedSlotList.size()){
            return routineResponseList;
        }

        // 활성화된 옵션 리스트에서 UNCHECKED 루틴들만 남겨놓음
        for(Routine routine : routineListInDB){
            if (activatedSlotList.contains(routine.getCategory())) {
                activatedSlotList.remove(routine.getCategory());
            }
        }

        // 남은 활성화된 옵션들로 응답에 UNCHECKED 루틴들 추가
        routineResponseList.addAll(activatedSlotList.stream()
                .map(category -> uncheckedRoutineResponse(LocalDate.now(), category, slotQueryService.getTargetAmountOrNull(slot, category)))
                .toList());

        return routineResponseList;
    }

    // ------------------------------ old -----------------------------------
//
//    // @과거의 루틴 조회
//    public List<RoutineResponse> getPastRoutines(Long petId, LocalDate entryDate) {
//        Pet pet = petRepository.findById(petId)
//                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
//        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
//                .orElse(null); // 해당 날짜의 entry가 없다면 빈 리스트 반환
//
//        /**
//         * 과거의 루틴 응답 리스트 생성 방법
//         * DB에 저장된 CHECKED, MEMO, UNCHECKED 루틴 리스트 응답
//         */
//        // DB에 저장된 루틴 리스트를 조회
//        List<Routine> routineListInDB = new ArrayList<>(routineRepository.findAllByEntry(entry));
//
//        // 루틴 응답 리스트 생성
//        return new ArrayList<>(routineListInDB.stream()
//                .map(this::createRoutineResponse)
//                .toList());
//    }
//
//
//    // @오늘의 루틴 조회
//    public List<RoutineResponse> getTodayRoutines(Long petId, LocalDate entryDate) {
//        Pet pet = petRepository.findById(petId)
//                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
//        Slot slot = pet.getSlot();
//
//        // 오늘 날짜의 엔트리 조회
//        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
//                .orElse(null);
//
//
//        // 루틴 응답 리스트
//        List<RoutineResponse> routineResponseList = new ArrayList<>();
//
//        /**
//         * 오늘의 루틴 응답 리스트 생성 방법
//         * 1. DB에 저장된 CHECKED, MEMO 루틴을 응답에 추가
//         * 2. 슬롯 활성화된 옵션 중에 DB에 없는 루틴을 UNCHECKED로 간주하여 응답에 추가
//        */
//        // 1. DB에 저장된 CHECKED, MEMO 루틴 리스트를 추가
//        List<Routine> routineListInDB = new ArrayList<>();
//        routineListInDB.addAll(routineRepository.findAllByEntry(entry));
//        routineResponseList.addAll(routineListInDB.stream()
//                .map(this::createRoutineResponse)
//                .toList());
//
//        // 2. 현재 활성화된 슬롯 옵션 중에 UNCHECKED인 루틴 리스트를 추가
//        // 활성화된 슬롯 옵션명 조회
//        List<String> activatedSlotOptions = slotService.getActivatedSlotOptions(slot);
//
//        // DB에 저장된 루틴이 활성화된 슬롯 옵션 개수와 같으면(루틴 완료) 이대로 반환
//        if(routineResponseList.size() == activatedSlotOptions.size()){
//            return routineResponseList;
//        }
//
//        // 활성화된 옵션 리스트에서 UNCHECKED 루틴들만 남겨놓음
//        for(Routine routine : routineListInDB){
//            if (activatedSlotOptions.contains(routine.getCategory())) {
//                activatedSlotOptions.remove(routine.getCategory());
//            }
//        }
//
//        // 남은 활성화된 옵션들로 응답에 UNCHECKED 루틴들 추가
//        routineResponseList.addAll(activatedSlotOptions.stream()
//                .map(category -> uncheckedRoutineResponse(entryDate, category, getTargetAmountByCategory(slot, category)))
//                .toList());
//
//       return routineResponseList;
//    }
}
