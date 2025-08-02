package ject.petfit.domain.routine.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.routine.dto.request.RoutineMemoRequest;
import ject.petfit.domain.routine.dto.response.DailyAllRoutineResponse;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.exception.RoutineErrorCode;
import ject.petfit.domain.routine.exception.RoutineException;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.exception.SlotErrorCode;
import ject.petfit.domain.slot.exception.SlotException;
import ject.petfit.domain.slot.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final PetRepository petRepository;
    private final EntryService entryService;
    private final EntryRepository entryRepository;
    private final SlotService slotService;

    // ------------------------------ 루틴 공통 메서드 -----------------------------------
    // 카테고리에 따른 슬롯 목표량 or null 반환
    public Integer getTargetAmountByCategory(Slot slot, String category) {
        return switch (category) {
            case "feed" -> slot.getFeedAmount();
            case "water" -> slot.getWaterAmount();
            case "walk" -> slot.getWalkAmount();
            case "potty", "skin", "dental" -> null;
            default -> throw new RoutineException(RoutineErrorCode.ROUTINE_CATEGORY_NOT_FOUND);
        };
    }

    // 루틴 해제 후 메모 존재 여부로 entry 업데이트
    public void updateEntryMemo(Entry entry) {
        if (!routineRepository.existsByEntryAndStatus(entry, RoutineStatus.MEMO)) {
            entry.updateMemoFalse();
        }
    }

    // 루틴 응답 생성
    public RoutineResponse createRoutineResponse(Routine routine) {
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

    // 오늘의 루틴 UNCHECKED 응답
    public RoutineResponse uncheckedRoutineResponse(LocalDate todayDate, String category, Integer targetAmount) {
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

    // ------------------------------ API 메서드 -----------------------------------

    // 과거의 루틴 조회
    public List<RoutineResponse> getPastRoutines(Long petId, LocalDate entryDate) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
                .orElse(null); // 해당 날짜의 entry가 없다면 빈 리스트 반환

        /**
         * 과거의 루틴 응답 리스트 생성 방법
         * DB에 저장된 CHECKED, MEMO, UNCHECKED 루틴 리스트 응답
         */
        // DB에 저장된 루틴 리스트를 조회
        List<Routine> routineListInDB = new ArrayList<>(routineRepository.findAllByEntry(entry));

        // 루틴 응답 리스트 생성
        return new ArrayList<>(routineListInDB.stream()
                .map(this::createRoutineResponse)
                .toList());
    }


    // 오늘의 루틴 조회
    public List<RoutineResponse> getTodayRoutines(Long petId, LocalDate entryDate) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Slot slot = pet.getSlot();

        // 오늘 날짜의 엔트리 조회
        // 홈화면에 처음 접속하면 오늘 날짜의 Entry DB를 생성
        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate)
                .orElse(null);

        // 루틴 응답 리스트
        List<RoutineResponse> routineResponseList = new ArrayList<>();

        /**
         * 오늘의 루틴 응답 리스트 생성 방법
         * 1. DB에 저장된 CHECKED, MEMO 루틴을 응답에 추가
         * 2. 슬롯 활성화된 옵션 중에 DB에 없는 루틴을 UNCHECKED로 간주하여 응답에 추가
        */
        // 1. DB에 저장된 CHECKED, MEMO 루틴 리스트를 추가
        List<Routine> routineListInDB = new ArrayList<>();
        routineListInDB.addAll(routineRepository.findAllByEntry(entry));
        routineResponseList.addAll(routineListInDB.stream()
                .map(this::createRoutineResponse)
                .toList());

        // 2. 현재 활성화된 슬롯 옵션 중에 UNCHECKED인 루틴 리스트를 추가
        // 활성화된 슬롯 옵션명 조회
        List<String> activatedSlotOptions = slotService.getActivatedSlotOptions(slot);

        // DB에 저장된 루틴이 활성화된 슬롯 옵션 개수와 같으면(루틴 완료) 이대로 반환
        if(routineResponseList.size() == activatedSlotOptions.size()){
            return routineResponseList;
        }

        // 활성화된 옵션 리스트에서 UNCHECKED 루틴들만 남겨놓음
        for(Routine routine : routineListInDB){
            if (activatedSlotOptions.contains(routine.getCategory())) {
                activatedSlotOptions.remove(routine.getCategory());
            }
        }

        // 남은 활성화된 옵션들로 응답에 UNCHECKED 루틴들 추가
        routineResponseList.addAll(activatedSlotOptions.stream()
                .map(category -> uncheckedRoutineResponse(entryDate, category, getTargetAmountByCategory(slot, category)))
                .toList());

       return routineResponseList;
    }

    // 루틴 체크(V) 완료
    @Transactional
    public String checkRoutine(Long petId, LocalDate entryDate, String category) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 활성화된 슬롯의 요청인지 확인
        slotService.isCategorySlotActivated(pet.getSlot(), category);

        // 해당 날짜의 entry가 있으면 조회 없으면 생성
        Entry entry = entryService.getOrCreateEntry(pet, entryDate);

        // 카테고리 따라 목표량 달라짐
        Integer targetAmount = getTargetAmountByCategory(pet.getSlot(), category);

        // 루틴 조회해서 없으면 생성 있으면 수정(메모->체크 수정하는 케이스)
        Routine routine = routineRepository.findByEntryAndCategory(entry, category)
                .orElseGet(() -> {
                    // 루틴이 없으면 새로 생성
                    return Routine.builder()
                            .entry(entry)
                            .category(category)
                            .targetAmount(targetAmount)
                            .build();
                });

        // 루틴 체크로 설정
        routine.updateStatus(RoutineStatus.CHECKED); // 상태를 체크로 변경
        routine.updateActualAmount(targetAmount); // 실제량을 목표량으로 설정
        routine.updateContent(null); // 내용은 null로 설정 (메모가 없으므로)

        // 루틴 저장
        routineRepository.save(routine);

        // 기록 여부 업데이트
        entry.updateCheckedTrue();

        return "CHECKED";
    }

    // 루틴 세모
    @Transactional
    public RoutineResponse createRoutineMemo(Long petId, LocalDate entryDate, String category, RoutineMemoRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 활성화된 슬롯의 요청인지 확인
        slotService.isCategorySlotActivated(pet.getSlot(), category);

        // 해당 날짜의 entry가 있으면 조회 없으면 생성
        Entry entry = entryService.getOrCreateEntry(pet, entryDate);

        // 카테고리 따라 목표량 달라짐
        Integer targetAmount = getTargetAmountByCategory(pet.getSlot(), category);

        // 루틴 조회해서 없으면 생성 있으면 수정(체크->메모 수정하는 케이스)
        Routine routine = routineRepository.findByEntryAndCategory(entry, category)
                .orElseGet(() -> {
                    // 루틴이 없으면 새로 생성
                    return Routine.builder()
                            .entry(entry)
                            .category(category)
                            .targetAmount(targetAmount)
                            .build();
                });
        // 루틴 메모로 설정
        routine.updateStatus(RoutineStatus.MEMO); // 상태를 메모로 변경
        routine.updateActualAmount(request.getActualAmount()); // 실제량은 요청에서 가져옴
        routine.updateContent(request.getContent()); // 내용은 요청에서 가져옴

        // 루틴 저장
        routineRepository.save(routine);

        // 기록 여부 업데이트
        entry.updateMemoTrue();

        return RoutineResponse.from(routine);
    }

    // 루틴 해제
    @Transactional
    public String uncheckRoutine(Long petId, LocalDate entryDate, String category) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 활성화된 슬롯의 요청인지 확인
        slotService.isCategorySlotActivated(pet.getSlot(), category);

        // 해당 날짜의 entry가 없으면 예외 발생
        Entry entry = entryService.getEntry(pet, entryDate);

        // 루틴 조회
        Routine routine = routineRepository.findByEntryAndCategory(entry, category)
                .orElseThrow(() -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));

        // 루틴 삭제
        routineRepository.delete(routine);


        return "UNCHECKED";
    }
}
