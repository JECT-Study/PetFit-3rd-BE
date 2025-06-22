package ject.petfit.domain.entry.repository;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    boolean existsByPetAndEntryDate(Pet pet, LocalDate entryDate);

    Entry findByPetAndEntryDate(Pet pet, LocalDate targetDate);

    // isScheduled
    List<Entry> findAllByPetAndIsScheduledTrue(Pet pet);
    List<Entry> findAllByPetAndIsScheduledTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);

    //isRemarked
    List<Entry> findAllByPetAndIsRemarkedTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);
}
