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

    // ~ 추후 chunk 기반으로 변경해야함
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate today = LocalDate.now().minusDays(1);
        List<Entry> entryList = entryRepository.findAllByEntryDate(today); /** 1. 오늘날짜의 Entry를 모두 조회 */
        HashMap<Long, Boolean> updatedPets = new HashMap<>();
        for( Entry entry : entryList) {
            boolean isRoutineCompleted =  todayRoutineFacade.todayRoutineSave(entry);
            updatedPets.put(entry.getPet().getId(), isRoutineCompleted);
        }

        // 업데이트된 펫 정보 로그 출력
        // ~ 추후 트랜잭션 성공/실패도 나눠서 로그 기록 및 이메일 보내야함
        log.info("=========================================================");
        log.info("({}, {}개) 오늘의 루틴 기록이 업데이트 되었습니다. ",today, updatedPets.size());
        updatedPets.forEach((petId, isRoutineCompleted) ->
            log.info("오늘의 루틴 업데이트: PetId = {}, 루틴 완료 여부 = {}", petId, isRoutineCompleted?"완료":"미완료")
        );
        log.info("=========================================================");

        chunkContext.getStepContext().getStepExecution().getJobExecution()
                .getExecutionContext().put("updatedPets", updatedPets);

        return RepeatStatus.FINISHED;
    }
}
