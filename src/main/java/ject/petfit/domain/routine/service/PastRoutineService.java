package ject.petfit.domain.routine.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.routine.dto.request.RoutineUpdateRequest;
import ject.petfit.domain.routine.dto.response.RoutineResponse;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PastRoutineService {
    private final RoutineRepository routineRepository;
    private final PetRepository petRepository;
    private final EntryRepository entryRepository;


    @Transactional
    public RoutineResponse updateRoutine(Long routineId, @Valid RoutineUpdateRequest routineUpdateRequest) {
        return null;
    }


}
