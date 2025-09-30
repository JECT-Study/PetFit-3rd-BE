package ject.petfit.global.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final Step todayRoutineSaveStep;
    private final Step sendUpdateCompleteEmailStep;
    private final Step todaySlotHistorySaveStep;

    @Bean
    public Job todayRoutineSaveJob(JobRepository jobRepository) {
        return new JobBuilder("todayRoutineSaveJob", jobRepository)
                .start(todayRoutineSaveStep)
                .next(sendUpdateCompleteEmailStep)
                .build();
    }

    @Bean
    public Job todaySlotHistorySaveJob(JobRepository jobRepository) {
        return new JobBuilder("todaySlotSaveJob", jobRepository)
                .start(todaySlotHistorySaveStep)
                .build();
    }
}
