package ject.petfit.domain.entry.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryCommandService {
    private final EntryRepository entryRepository;

    // ----------------------------- 엔트리 공통 메서드 -----------------------------------
    // (Pet, LocalDate)의 entry가 있으면 조회, 없으면 생성
    @Transactional
    public Entry getOrCreateEntry(Pet pet, LocalDate localDate) {
        return entryRepository.findByPetAndEntryDate(pet, localDate)
                    .orElseGet(() -> createEntry(pet, localDate));
    }

    // Entry 초기화
    @Transactional
    public Entry createEntry(Pet pet, LocalDate localDate) {
        return entryRepository.save(Entry.builder()
                .pet(pet)
                .entryDate(localDate)
                .isChecked(false)
                .isMemo(false)
                .isRemarked(false)
                .isScheduled(false)
                .isCompleted(false)
                .build());
    }

}
