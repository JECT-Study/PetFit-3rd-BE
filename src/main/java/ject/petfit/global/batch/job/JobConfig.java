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

    @Bean
    public Job todayRoutineSaveJob(JobRepository jobRepository) {
        // Job 정의 및 설정을 여기에 추가합니다.
        return new JobBuilder("todayRoutineSaveJob", jobRepository)
                .start(todayRoutineSaveStep)
                .build();
    }
}
