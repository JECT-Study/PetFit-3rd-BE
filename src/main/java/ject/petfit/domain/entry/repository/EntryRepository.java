package ject.petfit.domain.entry.repository;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    boolean existsByPetAndEntryDate(Pet pet, LocalDate entryDate);

//    Entry findByPetAndEntryDate(Pet pet, LocalDate targetDate);
    Optional<Entry> findByPetAndEntryDate(Pet pet, LocalDate entryDate);

    // isScheduled
    List<Entry> findAllByPetAndIsScheduledTrue(Pet pet);
    List<Entry> findAllByPetAndIsScheduledTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);

    //isRemarked
    List<Entry> findAllByPetAndIsRemarkedTrueAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);

    List<Entry> findAllByPetAndEntryDateBetween(Pet pet, LocalDate startDate, LocalDate endDate);
}
