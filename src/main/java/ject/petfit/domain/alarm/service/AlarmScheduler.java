package ject.petfit.domain.alarm.service;

import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.alarm.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmScheduler { // ToDo: Batch로 옮기기
    private final AlarmRepository alarmRepository;
    private final SseEmitterService sseEmitterService;

    // 3초마다 실행, 예약된 알람 전송
    // 해당 시간이 지난 알람 중 아직 전송되지 않은 알람들을 여러개 보내놓고
    // 프론트에서 웹 브라우저 시간에 맞춰 알람을 띄우도록 처리
    // ToDo: 실제 운영환경에서는 DB 부하를 고려 20초로 변경예정

    @Scheduled(fixedRate = 3000)
    public void sendScheduledAlarms() {

        // 기존 알람 처리 로직
        LocalDateTime nowMinuteParsed = LocalDateTime.now().withSecond(0).withNano(0);
        List<Alarm> dueAlarms = alarmRepository.findByTargetDateTimeAndIsReadFalse(nowMinuteParsed);

        for (Alarm alarm : dueAlarms) {
            sseEmitterService.sendAlarm(alarm);
            alarm.markAsSent();
            alarmRepository.save(alarm);
        }
    }

    // ToDo: 30일 지난 알람 자동 삭제

}
