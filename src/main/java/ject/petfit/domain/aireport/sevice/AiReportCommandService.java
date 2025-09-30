package ject.petfit.domain.aireport.sevice;

import jakarta.validation.constraints.NotNull;
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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AiReportCommandService {

    private final AiReportRepository aiReportRepository;
    private final PetQueryService petQueryService;

    public AiReport saveAiReport(
        @NotNull Long petId,
        String summaryTitle, String aiReportContent,
        LocalDate startDate, LocalDate endDate) {
        Pet pet = petQueryService.getPetOrThrow(petId);

        return aiReportRepository.save(AiReport.builder()
                        .title(summaryTitle)
                        .content(aiReportContent)
                        .startDate(startDate)
                        .endDate(endDate)
                        .pet(pet)
                        .build());
    }

    public void deleteAiReport(Long reportId) {
        if (!aiReportRepository.existsById(reportId)) {
            throw new AiReportException(AiReportErrorCode.AI_REPORT_NOT_FOUND);
        }
        aiReportRepository.deleteById(reportId);
    }
}