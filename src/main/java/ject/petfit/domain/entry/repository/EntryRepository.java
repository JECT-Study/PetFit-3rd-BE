package ject.petfit.domain.entry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ject.petfit.domain.entry.entity.Entry;

import java.time.LocalDate;
import java.util.List;

import ject.petfit.domain.pet.entity.Pet;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    boolean existsByPetAndEntryDate(Pet pet, LocalDate entryDate);

    Entry findByPetAndEntryDate(Pet pet, LocalDate targetDate);

    // isScheduled
    List<Entry> findAllByPetAndIsScheduledTrue(Pet pet);
    List<Entry> findAllByPetAndIsScheduledTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);

    //isRemarked
    List<Entry> findAllByPetAndIsRemarkedTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);
}
