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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final PetRepository petRepository;

    // ----------------------------- 엔트리 공통 메서드 -----------------------------------
    // (펫ID & 날짜)의 entry가 있으면 조회, 없으면 생성 - 다른 서비스에서 사용
    @Transactional
    public Entry getOrCreateEntry(Pet pet, LocalDate entryDate) {
        Entry entry;
        if(!isEntryExist(pet, entryDate)) {
            entry = createEntry(pet, entryDate);
        }else{
            entry = getEntry(pet, entryDate);
        }
        return entry;
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
        return entryRepository.findByPetAndEntryDate(pet, targetDate);
    }

    // ------------------------------ API 메서드 -----------------------------------
    // 월간 루틴체크, 메모, 특이사항, (일정) 유무 조회
    public List<EntryExistsResponse> getMonthlyEntries(Long petId, LocalDate month) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));

        LocalDate startDate = month.withDayOfMonth(1);
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
}

