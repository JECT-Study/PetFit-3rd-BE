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
public class AlarmScheduler {
    private final AlarmRepository alarmRepository;
    private final SseEmitterService sseEmitterService;

    // 3초마다 실행, 예약된 알람 전송
    // 해당 시간이 지난 알람 중 아직 전송되지 않은 알람들을 여러개 보내놓고
    // 프론트에서 웹 브라우저 시간에 맞춰 알람을 띄우도록 처리
    // ToDo: 실제 운영환경에서는 DB 부하를 고려 1분으로 변경예정
    @Scheduled(fixedRate = 3000)
    public void sendScheduledAlarms() {
//        LocalDateTime nowMinute = LocalDateTime.now().withSecond(0).withNano(0);
//        List<Alarm> dueAlarms = alarmRepository.findByTargetDateTimeBeforeAndSentFalse(nowMinute);
        // 현재 시각 기준, 1분 전부터 0분 후까지의 알람만 조회
//        LocalDateTime from = nowMinute.minusMinutes(1);
//        LocalDateTime to = nowMinute;
//        List<Alarm> dueAlarms = alarmRepository.findByTargetDateTimeBetweenAndIsReadFalse(from, to);

        // 기존 알람 처리 로직
        LocalDateTime nowMinuteParsed = LocalDateTime.now().withSecond(0).withNano(0);
        List<Alarm> dueAlarms = alarmRepository.findByTargetDateTimeAndIsReadFalse(nowMinuteParsed);

        for (Alarm alarm : dueAlarms) {
            sseEmitterService.sendAlarm(alarm);
            alarm.markAsSent();
            alarmRepository.save(alarm);
        }
    }

}
/*
AlarmScheduler가 30초마다 실행되어,
예약 시간이 지난 알람 중 아직 읽지 않은(read=false) 알람들만 조회 후 SSE로 전송합니다.
알람이 전송된 후에는 sent=true로 변경 - 의미없을듯

이렇게 하면 알람 등록 즉시가 아니라, 예약된 시점에 맞춰 자동으로 프론트에 알림을 보낼 수 있습니다.
실제 운영 환경에서는 더 정교한 스케줄링(예: Quartz, 외부 메시지 큐 등)을 사용할 수 있습니다.

백엔드에서 겹치는 시각 알람은 못받게 처리할거임
*/
