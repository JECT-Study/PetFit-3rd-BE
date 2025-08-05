package ject.petfit.global.batch.tasklet;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.routine.facade.TodayRoutineFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodayRoutineSaveTasklet implements Tasklet {
    private final EntryRepository entryRepository;
    private final TodayRoutineFacade todayRoutineFacade;

    // 추후 chunk 기반으로 변경해야함
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate today = LocalDate.now().minusDays(1);
//        List<Entry> entryList = entryRepository.findAllByEntryDate(today);
//        HashMap<Long, String> updatedPets = new HashMap<>();
//        for( Entry entry : entryList) {
//            todayRoutineFacade.todayRoutineSave(entry);
//        }

        HashMap<Long, String> updatedPets = todayRoutineFacade.todayRoutineSave(today);
        // 업데이트된 펫 정보 로그 출력
        // ~ 추후 성공/실패도 나눠야함

        log.info("=========================================================");
        log.info("({}, {}개) 오늘의 루틴 기록이 업데이트 되었습니다. ",today, updatedPets.size());
        updatedPets.forEach((petId, petName) ->
            log.info("오늘의 루틴 업데이트: PetId = {}, PetName = {}", petId, petName)
        );
        log.info("=========================================================");
        return RepeatStatus.FINISHED;
    }
}
