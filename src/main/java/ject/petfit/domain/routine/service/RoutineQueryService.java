package ject.petfit.domain.routine.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import ject.petfit.domain.routine.exception.RoutineErrorCode;
import ject.petfit.domain.routine.exception.RoutineException;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.entity.SlotHistory;
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

    public boolean existsByEntry(Entry entry) {
        return routineRepository.existsByEntry(entry);
    }

    public List<Routine> getDailyRoutines(Entry entry) {
        return routineRepository.findAllByEntry(entry);
    }

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

    // Entry가 있는 과거 루틴 조회
    public List<RoutineResponse> getPastRoutinesByEntry(Entry entry) {
        return new ArrayList<>(getDailyRoutines(entry).stream()
                .map(this::createRoutineResponse)
                .toList());
    }

    // Entry가 없는 과거 루틴 조회 - 활성화된 슬롯 & 미체크 루틴 응답
    public List<RoutineResponse> getPastRoutinesBySlotHistory(SlotHistory slotHistory) {
        List<RoutineResponse> routineResponseList = new ArrayList<>();
        if(slotHistory.isFeedActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "feed", slotHistory.getFeedAmount()));
        }
        if(slotHistory.isWaterActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "water", slotHistory.getWaterAmount()));
        }
        if(slotHistory.isWalkActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "walk", slotHistory.getWalkAmount()));
        }
        if(slotHistory.isPottyActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "potty", null));
        }
        if(slotHistory.isDentalActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "dental", null));
        }
        if(slotHistory.isSkinActivated()){
            routineResponseList.add(uncheckedRoutineResponse(slotHistory.getRecordDate(), "skin", null));
        }
        return routineResponseList;
    }


    /** 오늘의 루틴 리스트 조회
     * 오늘의 루틴 응답 리스트 생성 방법
     * 1. DB에 저장된 CHECKED, MEMO 루틴을 응답 리스트 DTO에 추가
     * 2. 슬롯 활성화된 옵션 중에 DB에 없는 루틴을 UNCHECKED로 간주하여 응답 DTO에 추가
     */
    public List<RoutineResponse> getTodayRoutines(Optional<Entry> entry, Slot slot) {
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

    /** 일간 루틴 리스트 조회
     1. 오늘 날짜로 조회할 경우
         1) 홈화면에 처음 접속한 경우
         - create 작업한게 없으므로 오늘의 Entry도 null 상태
         - 슬롯 활성화된 미체크 루틴 리스트 DTO 생성하여 반환

         2) 루틴 CHECKED, MEMO 상태의 루틴이 있는 경우
         - DB에 저장된 CHECKED, MEMO 루틴 리스트 응답
         - Unchecked 루틴은 과거 날짜에만 DB에 저장되므로, 오늘 날짜의 루틴 조회 시에는 제외됨

     2. 과거 날짜로 조회할 경우
         1) 루틴 기록이 있는 경우
            - DB에 저장된 CHECKED, MEMO, UNCHECKED 루틴 리스트 응답

         2) 루틴 기록이 없는 경우
            - 슬롯 기록이 있는 경우: 슬롯 활성화된 미체크 루틴 리스트 DTO 생성하여 반환
            - 슬롯 기록이 없는 경우(가입일 이전): 빈 리스트 반환

     3. 미래 날짜로 조회할 경우 빈 리스트 반환
     */
    public List<RoutineResponse> getDailyRoutines(Pet pet, Optional<Entry> entry, LocalDate date) {
        if (date.isEqual(LocalDate.now())) {
            return getTodayRoutines(entry, pet.getSlot());
        }else if( date.isBefore(LocalDate.now())) {
            if(entry.isPresent() && existsByEntry(entry.get())) { // 루틴 기록이 있는날
                return getPastRoutinesByEntry(entry.get());
            }
            else{
                Optional<SlotHistory> slotHistory = slotQueryService.getSlotHistoryOptional(pet, date);
                if(slotHistory.isPresent()) { // 슬롯 기록이 있는날
                    return getPastRoutinesBySlotHistory(slotHistory.get());
                }else{ // 슬롯 기록이 없는날(가입일 이전)
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>(); // 미래 날짜 조회는 빈 리스트 처리
    }

}
