package ject.petfit.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ject.petfit.domain.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
