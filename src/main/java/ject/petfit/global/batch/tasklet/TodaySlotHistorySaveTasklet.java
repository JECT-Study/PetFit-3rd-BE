package ject.petfit.global.batch.tasklet;

import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.slot.service.SlotCommandService;
import ject.petfit.domain.slot.service.SlotQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodaySlotHistorySaveTasklet implements Tasklet {
    private final PetRepository petRepository;
    private final SlotCommandService slotCommandService;
    private final SlotQueryService slotQueryService;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate today = LocalDate.now().minusDays(1);
        List<Pet> pets = petRepository.findAll();
        for (Pet pet : pets) {
            if(slotQueryService.getSlotOptional(pet).isPresent()) {
                slotCommandService.saveTodaySlotHistory(pet.getSlot(), today);
            }
        }
        log.info("오늘의 슬롯 기록 저장 완료 {} {}개 ", LocalDate.now(), slotQueryService.countSlotHistoriesByDate(today));
        return RepeatStatus.FINISHED;
    }
}
