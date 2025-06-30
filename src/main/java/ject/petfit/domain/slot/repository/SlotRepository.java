package ject.petfit.domain.slot.repository;

import ject.petfit.domain.slot.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SlotRepository extends JpaRepository<Slot, Long> {

    boolean existsByPetId(Long petId);
}
