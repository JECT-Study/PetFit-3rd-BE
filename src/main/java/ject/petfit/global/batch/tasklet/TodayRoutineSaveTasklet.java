package ject.petfit.global.batch.tasklet;

import ject.petfit.domain.routine.facade.TodayRoutineFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodayRoutineSaveTasklet implements Tasklet {
    private final TodayRoutineFacade todayRoutineFacade;

    // 추후 chunk 기반으로 변경해야함
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate today = LocalDate.now().minusDays(1);
        todayRoutineFacade.todayRoutineSave(today);
        log.info("(Batch) 오늘의 루틴 기록이 업데이트 되었습니다. - " + today);
        return RepeatStatus.FINISHED;
    }
}
