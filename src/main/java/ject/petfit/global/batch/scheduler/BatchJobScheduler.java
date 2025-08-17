package ject.petfit.global.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BatchJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job todayRoutineSaveJob;

    // 매일 자정 00시 00분에 실행 (cron = 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 0 * * *")
    public void runJob() throws Exception {
        System.out.println("실행시작");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(todayRoutineSaveJob, jobParameters);
    }

    // 테스트용
    @Scheduled(cron = "0 00 16 * * *")
    public void runJobTest() throws Exception {
        System.out.println("BatchJobScheduler.runJobTest() called - " + LocalDateTime.now());
    }
}
