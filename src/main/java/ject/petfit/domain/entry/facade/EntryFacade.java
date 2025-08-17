package ject.petfit.domain.entry.facade;

import ject.petfit.domain.entry.dto.EntryDailyResponse;
import ject.petfit.domain.entry.dto.EntryExistsResponse;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.service.RoutineQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EntryFacade {
    private final PetQueryService petQueryService;
    private final EntryQueryService entryQueryService;
    private final RoutineQueryService routineQueryService;

    // 월간 루틴체크, 메모, 특이사항, (일정) 유무 조회
    public List<EntryExistsResponse> getMonthlyEntries(Long petId, String month) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Entry> entries = entryQueryService.getEntriesByPetAndDateRange(pet, startDate, endDate);
        return entries.stream()
                .map(EntryExistsResponse::from)
                .toList();
    }

    // 주간 루틴, 특이사항 조회
    public List<EntryDailyResponse> getWeeklyEntries(Long petId, LocalDate startDate) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        LocalDate endDate = startDate.plusDays(6);
        List<Entry> entries = entryQueryService.getEntriesByPetAndDateRange(pet, startDate, endDate);
        return entries.stream()
                .map(EntryDailyResponse::from)
                .toList();
    }

    // 일간 엔트리 조회(특이사항+루틴 리스트)
    public EntryDailyResponse getDailyEntries(Long petId, LocalDate date) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        Optional<Entry> entry = entryQueryService.getEntryOptional(pet, date);

        // getDailyRoutines의 오늘/과거 날짜에 따른 루틴 조회 로직을 재사용
        List<RoutineResponse> routineResponseList = new ArrayList<>();
        if (date.isEqual(LocalDate.now())) {
            routineResponseList = routineQueryService.getTodayRoutines(entry, pet.getSlot());
        }else if( date.isBefore(LocalDate.now())) {
            routineResponseList = routineQueryService.getPastRoutines(entry);
        }

        // entry가 없는 경우 특이사항 X, 루틴 리스트 없거나(과거) 모두 Unchecked(오늘)로 응답
        // entry가 있는 경우 해당 entry의 특이사항들과 루틴 리스트 응답
        return entry.isEmpty() ?
                EntryDailyResponse.ofNull(date, routineResponseList)
                : EntryDailyResponse.of(entry.get(), routineResponseList);
    }
}
