package ject.petfit.domain.entry.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.dto.EntryDailyResponse;
import ject.petfit.domain.entry.dto.EntryExistsResponse;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.exception.EntryErrorCode;
import ject.petfit.domain.entry.exception.EntryException;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final PetRepository petRepository;
    private final RoutineRepository routineRepository;

    // ----------------------------- 엔트리 공통 메서드 -----------------------------------
    // (Pet, LocalDate)의 entry가 있으면 조회, 없으면 생성
    @Transactional
    public Entry getOrCreateEntry(Pet pet, LocalDate entryDate) {
        if(!isEntryExist(pet, entryDate)) {
            return createEntry(pet, entryDate);
        }
        return entryRepository.findByPetAndEntryDate(pet, entryDate)
                    .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_IMPLEMENTED)); // 작성했지만 이 예외는 실행되면 안됨
    }

    // Entry 초기화
    @Transactional
    public Entry createEntry(Pet pet, LocalDate entryDate) {
        if (isEntryExist(pet, entryDate)) {
            throw new EntryException(EntryErrorCode.ENTRY_ALREADY_EXISTS);
        }
        return entryRepository.save(Entry.builder()
                .pet(pet)
                .entryDate(entryDate)
                .isChecked(false)
                .isMemo(false)
                .isRemarked(false)
                .isScheduled(false)
                .build());
    }

    // Entry 존재 여부 확인 (펫, 조회 날짜) // 캐시 예정
    public Boolean isEntryExist(Pet pet, LocalDate entryDate) {
        if (pet == null || entryDate == null) {
            throw new EntryException(EntryErrorCode.INVALID_ENTRY_REQUEST);
        }
        return entryRepository.existsByPetAndEntryDate(pet, entryDate);
    }

    // Entry 조회 (펫, 조회 날짜)
    public Entry getEntry(Pet pet, LocalDate targetDate) {
        if (!isEntryExist(pet, targetDate)) {
            throw new EntryException(EntryErrorCode.ENTRY_NOT_FOUND);
        }
        return entryRepository.findByPetAndEntryDate(pet, targetDate)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));
    }

    // ------------------------------ API 메서드 -----------------------------------
    // 월간 루틴체크, 메모, 특이사항, (일정) 유무 조회
    public List<EntryExistsResponse> getMonthlyEntries(Long petId, String month) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));

        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Entry> entries = entryRepository.findAllByPetAndEntryDateBetween(pet, startDate, endDate);

        return entries.stream()
                .map(EntryExistsResponse::from)
                .toList();
    }

    // 주간 루틴, 특이사항 조회
    public List<EntryDailyResponse> getWeeklyEntries(Long petId, LocalDate startDate) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));
        LocalDate endDate = startDate.plusDays(6);
        List<Entry> entries = entryRepository.findAllByPetAndEntryDateBetween(pet, startDate, endDate);


        return entries.stream()
                .map(EntryDailyResponse::from)
                .toList();
    }

    // 일간 특이사항 + 루틴 리스트 조회
    public EntryDailyResponse getDailyEntries(Long petId, LocalDate date, List<RoutineResponse> routineResponseList) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));
        Entry entry = entryRepository.findByPetAndEntryDate(pet, date)
                .orElse(null);

        return entry == null ? EntryDailyResponse.ofNull(date, routineResponseList) : EntryDailyResponse.of(entry, routineResponseList);

        /**
         *  오늘일 경우
         *  entry가 null || 루틴 DB에 오늘의 entry 데이터가 없을 경우 생성해서 반환
         *  루틴 DB에 오늘의 entry 데이터가 있을 경우 entry로 응답
         */
//        if (date.isEqual(LocalDate.now())) {
//            return entry == null || !routineRepository.existsByEntry(entry) ?
//                    EntryDailyResponse.ofNull(date, getTodayUNRoutines(petId, date)) :
//                    EntryDailyResponse.from(entry);
//        }

        /**
         * 과거의 경우
         * entry가 없을 경우 모든 여부 false와 빈 리스트로 응답
         * entry가 있을 경우 entry로 응답
         */
//        return entry == null ? EntryDailyResponse.fromNull(date) : EntryDailyResponse.from(entry);
    }
}
