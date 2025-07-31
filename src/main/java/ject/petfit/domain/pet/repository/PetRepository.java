package ject.petfit.domain.pet.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByIdIn(List<Long> petIds);

    List<Pet> findByMember(Member member);
}
