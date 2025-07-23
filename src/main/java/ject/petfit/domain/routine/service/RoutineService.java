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
import ject.petfit.domain.slot.entity.Slot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final PetRepository petRepository;
    private final EntryService entryService;
    private final EntryRepository entryRepository;

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

    // 루틴 해제 후 체크 존재 여부로 entry 업데이트
    public void updateEntryChecked(Entry entry) {
        if (!routineRepository.existsByEntryAndStatus(entry, RoutineStatus.CHECKED)) {
            entry.updateCheckedFalse();
        }
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

    // 루틴 unchecked 응답
    public RoutineResponse uncheckedRoutineResponse(LocalDate date, String category, Integer targetAmount) {
        return RoutineResponse.builder()
                .routineId(null)
                .category(category)
                .status(RoutineStatus.UNCHECKED)
                .targetAmount(targetAmount)
                .actualAmount(0)
                .content(null)
                .date(date)
                .build();
    }

    // ------------------------------ API 메서드 -----------------------------------

    // 루틴 일간 조회
    public DailyAllRoutineResponse getDailyRoutines(Long petId, LocalDate entryDate) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        Slot slot = pet.getSlot();

        // 슬롯 활성화되어 있으면 unchecked 루틴 생성, 슬롯 비활성화이면 null로 설정
        RoutineResponse feedRoutine = slot.isFeedActivated()?
                uncheckedRoutineResponse(entryDate, "feed", slot.getFeedAmount()) : null;
        RoutineResponse waterRoutine = slot.isWaterActivated()?
                uncheckedRoutineResponse(entryDate, "water", slot.getWaterAmount()) : null;
        RoutineResponse walkRoutine = slot.isWalkActivated()?
                uncheckedRoutineResponse(entryDate, "walk", slot.getWalkAmount()) : null;
        RoutineResponse pottyRoutine = slot.isPottyActivated()?
                uncheckedRoutineResponse(entryDate, "potty", null) : null;
        RoutineResponse dentalRoutine = slot.isDentalActivated()?
                uncheckedRoutineResponse(entryDate, "dental", null) : null;
        RoutineResponse skinRoutine = slot.isSkinActivated()?
                uncheckedRoutineResponse(entryDate, "skin", null) : null;

        // 해당 날짜의 루틴이 하나라도 있는지 조회
        // 기록이 없는 날이면 루틴 조회하지 않고 바로 반환
        Entry entry = entryRepository.findByPetAndEntryDate(pet, entryDate);
        if (entry == null || routineRepository.existsByEntry(entry)) {
            return DailyAllRoutineResponse.builder()
                    .feedRoutine(feedRoutine)
                    .waterRoutine(waterRoutine)
                    .walkRoutine(walkRoutine)
                    .pottyRoutine(pottyRoutine)
                    .dentalRoutine(dentalRoutine)
                    .skinRoutine(skinRoutine)
                    .build();
        }

        // 기록이 있는 날이면 루틴 있는 경우에만 상태 세팅
//        List<Routine> routines = routineRepository.findAllByEntry(entry);
        List<Routine> routineList = entry.getRoutines();

        // 해당 날짜 저장된 루틴 있는 경우에는 상태 세팅
        for (Routine routine : routineList) {
            switch (routine.getCategory()) {
                case "feed" -> feedRoutine = createRoutineResponse(routine);
                case "water" -> waterRoutine = createRoutineResponse(routine);
                case "walk" -> walkRoutine = createRoutineResponse(routine);
                case "potty" -> pottyRoutine = createRoutineResponse(routine);
                case "dental" -> dentalRoutine = createRoutineResponse(routine);
                case "skin" -> skinRoutine = createRoutineResponse(routine);
            }
        }

        return DailyAllRoutineResponse.builder()
                .feedRoutine(feedRoutine)
                .waterRoutine(waterRoutine)
                .walkRoutine(walkRoutine)
                .pottyRoutine(pottyRoutine)
                .dentalRoutine(dentalRoutine)
                .skinRoutine(skinRoutine)
                .build();
    }

    // 루틴 체크(V) 완료
    @Transactional
    public String checkRoutine(Long petId, LocalDate entryDate, String category) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

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
        // 해당 날짜의 entry가 없으면 예외 발생
        Entry entry = entryService.getEntry(pet, entryDate);

        // 루틴 조회
        Routine routine = routineRepository.findByEntryAndCategory(entry, category)
                .orElseThrow(() -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));

        // 루틴 삭제
        routineRepository.delete(routine);

        // 기록 여부 업데이트
        if (routine.getStatus() == RoutineStatus.CHECKED) {
            updateEntryChecked(entry);
        } else if (routine.getStatus() == RoutineStatus.MEMO) {
            updateEntryMemo(entry);
        }

        return "UNCHECKED";
    }
}
