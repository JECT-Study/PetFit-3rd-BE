package ject.petfit.global.batch.step;

import ject.petfit.global.batch.tasklet.SendUpdateCompleteEmailTasklet;
import ject.petfit.global.batch.tasklet.TodayRoutineSaveTasklet;
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

    @Bean
    public Step todayRoutineSaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("todayRoutineSaveStep", jobRepository)
                .tasklet(todayRoutineSaveTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step sendUpdateCompleteEmailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendUpdateCompleteEmailStep", jobRepository)
                .tasklet(sendUpdateCompleteEmailTasklet, transactionManager)
                .build();
    }
}
