package ject.petfit.domain.pet.repository;

import java.util.List;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByIdIn(List<Long> petIds);
}
