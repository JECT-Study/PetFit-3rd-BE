package ject.petfit.global.batch.tasklet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class SendUpdateCompleteEmailTasklet implements Tasklet {
    private final JavaMailSender mailSender;
    @Value("${app.swagger.server}")
    private String backendServer;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 이전 단계에서 업데이트된 펫 정보 load
        HashMap<Long, Boolean> updatedPets = (HashMap<Long, Boolean>) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext().get("updatedPets");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("petfitofficial@gmail.com");
        LocalDate today = LocalDate.now().minusDays(1);
        message.setSubject("(" + backendServer + ") 오늘의 루틴 업데이트 - " + today);
        int completedCount = (int) updatedPets.values().stream().filter(v -> v).count();
        int notCompletedCount = updatedPets.size() - completedCount;
        message.setText(
                "오늘의 루틴 업데이트가 완료되었습니다.\n\n"

                + "<" + today + ">\n"
                + "Entry 업데이트 총 개수: " + updatedPets.size() + "\n"
                + "완료: " + completedCount + "개, 미완료: " + notCompletedCount + "개\n\n"

                + "<업데이트된 펫 목록>\n"
                + (updatedPets.isEmpty() ? "없음" :
                    updatedPets.entrySet().stream()
                        .map(entry -> "PetId: " + entry.getKey() + ", 루틴 완료 여부: " + (entry.getValue() ? "완료" : "미완료"))
                        .reduce((s1, s2) -> s1 + "\n" + s2)
                        .get()
                )
        );
        mailSender.send(message);
        return RepeatStatus.FINISHED;
    }
}
