package ject.petfit.domain.aireport.repository;

import ject.petfit.domain.aireport.entity.AiReport;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiReportRepository extends JpaRepository<AiReport, Long> {
    List<AiReport> findByPetOrderByStartDateDesc(Pet pet);
}
