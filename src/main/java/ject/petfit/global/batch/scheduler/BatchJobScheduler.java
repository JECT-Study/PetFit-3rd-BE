package ject.petfit.global.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BatchJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job todayRoutineSaveJob;

    // 매일 오전 12시 01분에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 20 0 * * *")
    public void runJob() throws Exception {
        jobLauncher.run(todayRoutineSaveJob, new JobParameters());
    }

    // 테스트용
    @Scheduled(cron = "0 15 14 * * *")
    public void runJobTest() throws Exception {
        System.out.println("BatchJobScheduler.runJobTest() called - " + LocalDateTime.now());
    }
}
