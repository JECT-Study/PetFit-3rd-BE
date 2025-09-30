package ject.petfit.domain.aireport.sevice;

import ject.petfit.domain.aireport.entity.AiReport;
import ject.petfit.domain.aireport.exception.AiReportErrorCode;
import ject.petfit.domain.aireport.exception.AiReportException;
import ject.petfit.domain.aireport.repository.AiReportRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiReportQueryService {

    private final AiReportRepository aiReportRepository;
    private final PetQueryService petQueryService;

    public AiReport getAiReportOrThrow(Long reportId) {
        return aiReportRepository.findById(reportId)
                .orElseThrow(() -> new AiReportException(AiReportErrorCode.AI_REPORT_NOT_FOUND));
    }

    public List<AiReport> getAiReportsByPetId(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        return aiReportRepository.findByPetOrderByStartDateDesc(pet);
    }
}
