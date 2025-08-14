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

    // Entry 존재 여부 확인 (펫, 조회 날짜) // 캐시 예정
    public Boolean isEntryExist(Pet pet, LocalDate entryDate) {
        if (pet == null || entryDate == null) {
            throw new EntryException(EntryErrorCode.INVALID_ENTRY_REQUEST);
        }
        return entryRepository.existsByPetAndEntryDate(pet, entryDate);
    }




}
