package ject.petfit.domain.slot.repository;

import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SlotRepository extends JpaRepository<Slot, Long> {

    boolean existsByPetId(Long petId);
    boolean existsByPet(Pet pet);

    Optional<Slot> findByPet(Pet pet);
}
