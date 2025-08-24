package ject.petfit.global.batch.step;

import ject.petfit.global.batch.tasklet.SendUpdateCompleteEmailTasklet;
import ject.petfit.global.batch.tasklet.TodayRoutineSaveTasklet;
import ject.petfit.global.batch.tasklet.TodaySlotHistorySaveTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StepConfig {
    private final TodayRoutineSaveTasklet todayRoutineSaveTasklet;
    private final SendUpdateCompleteEmailTasklet sendUpdateCompleteEmailTasklet;
    private final TodaySlotHistorySaveTasklet todaySlotHistorySaveTasklet;

    @Bean
    public Step todayRoutineSaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("todayRoutineSaveStep", jobRepository)
                .tasklet(todayRoutineSaveTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step sendUpdateCompleteEmailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendUpdateCompleteEmailStep", jobRepository)
                .tasklet(sendUpdateCompleteEmailTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step todaySlotHistorySaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("todaySlotHistorySaveStep", jobRepository)
                .tasklet(todaySlotHistorySaveTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
