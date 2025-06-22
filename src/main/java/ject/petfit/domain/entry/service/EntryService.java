package ject.petfit.domain.entry.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.exception.EntryErrorCode;
import ject.petfit.domain.entry.exception.EntryException;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final PetRepository petRepository;

    // (펫ID & 날짜)의 entry가 있으면 반환, 없으면 생성해서 반환
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
        return entryRepository.existsByPetAndEntryDate(pet, entryDate);
    }

    // Entry 조회 (펫, 조회 날짜)
    public Entry getEntry(Pet pet, LocalDate targetDate) {
        return entryRepository.findByPetAndEntryDate(pet, targetDate);
    }
}

