package ject.petfit.domain.entry.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.exception.EntryErrorCode;
import ject.petfit.domain.entry.exception.EntryException;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryQueryService {
    private final EntryRepository entryRepository;

    private static final Long RECENT_ENTRY_LIMIT = 3L; // 최근 3일간의 엔트리 조회

    public Entry getEntryByPetAndLocalDateOrThrow(Pet pet, LocalDate localDate) {
        return entryRepository.findByPetAndEntryDate(pet, localDate)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_IMPLEMENTED));
    }

    public Entry getEntryOrThrow(Pet pet, LocalDate localDate) {
        return entryRepository.findByPetAndEntryDate(pet, localDate)
                .orElseThrow(() -> new EntryException(EntryErrorCode.ENTRY_NOT_FOUND));
    }

    public Entry getEntryOrNull(Pet pet, LocalDate localDate) {
        return entryRepository.findByPetAndEntryDate(pet, localDate)
                .orElse(null);
    }

    public List<Entry> getEntriesByPetAndDateRange(Pet pet, LocalDate startDate, LocalDate endDate) {
        return entryRepository.findAllByPetAndEntryDateBetween(pet, startDate, endDate);
    }

    public List<Entry> getRecentEntriesWithRemark(Pet pet){
        LocalDate today = LocalDate.now();
        return entryRepository.findAllByPetAndIsRemarkedTrueAndEntryDateBetween
                (pet, today, today.plusDays(RECENT_ENTRY_LIMIT - 1));
    }

    public List<Entry> getRecentEntriesWithSchedule(Pet pet) {
        LocalDate today = LocalDate.now();
        return entryRepository.findAllByPetAndIsScheduledTrueAndEntryDateBetween
                (pet, today, today.plusDays(RECENT_ENTRY_LIMIT - 1));
    }
}
