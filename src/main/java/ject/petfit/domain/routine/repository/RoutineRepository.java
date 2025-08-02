package ject.petfit.domain.routine.repository;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.enums.RoutineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Optional<Routine> findByEntryAndCategory(Entry entry, String category);
    boolean existsByEntryAndCategory(Entry entry, String category);

    boolean existsByEntry(Entry entry);

    boolean existsByEntryAndStatus(Entry entry, RoutineStatus status);

    List<Routine> findAllByEntry(Entry entry);

}
