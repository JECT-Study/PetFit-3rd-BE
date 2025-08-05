package ject.petfit.global.batch.tasklet;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendUpdateCompleteEmailTasklet implements Tasklet {
    private final JavaMailSender mailSender;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("asdf@company.com");
        message.setSubject("오늘의 업데이트 완료");
        message.setText("오늘의 업데이트가 정상적으로 완료되었습니다.");
        mailSender.send(message);
        return RepeatStatus.FINISHED;
    }
}
