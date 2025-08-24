package ject.petfit.domain.slot.repository;

import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.entity.SlotHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SlotHistoryRepository extends JpaRepository<SlotHistory, Long> {
    Optional<SlotHistory> findSlotHistoryByPetAndRecordDate(Pet pet, LocalDate localDate);
    int countByRecordDate(LocalDate localDate);
}
