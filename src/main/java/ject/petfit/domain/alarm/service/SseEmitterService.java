package ject.petfit.domain.alarm.service;

import ject.petfit.domain.alarm.dto.response.AlarmResponse;
import ject.petfit.domain.alarm.entity.Alarm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE(Server-Sent Events) 연결을 생성하고 관리하는 메서드
    public SseEmitter createEmitter(Long petId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 새로운 SseEmitter 객체를 생성합니다. (Long.MAX_VALUE는 연결 유지 시간을 의미)
        emitters.put(petId, emitter); // petId를 키로 사용하여 emitters 맵에 저장합니다.

        emitter.onCompletion(() -> emitters.remove(petId)); // 연결이 완료되면 emitters 맵에서 제거합니다.
        emitter.onTimeout(() -> emitters.remove(petId)); // 연결이 시간 초과되면 emitters 맵에서 제거합니다.

        // 연결 직후 브라우저가 onopen을 정상적으로 인식할 수 있도록 주석 이벤트를 즉시 전송.
        // SSE 프로토콜상, 연결이 맺어지면 서버는 즉시 최소한 한 번의 빈 이벤트(예: \n\n)라도 보내야
        // 브라우저가 연결을 "성공"으로 인식하고 프론트에서 EventSource  onopen을 호출한다
        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter; // 생성한 emitter를 반환하여 클라이언트와 실시간 통신에 사용
    }

    // 전달받은 알람을 SSE를 이벤트로 발행하여
    // 클라이언트에게 실시간으로 전송
    public void sendAlarm(Alarm alarm) {
        Long petId = alarm.getPet().getId();
        log.info("Sending alarm: {}", alarm.getTitle());
        SseEmitter emitter = emitters.get(petId);
        AlarmResponse alarmResponse = AlarmResponse.from(alarm);
        if (emitter != null) {
            try {
                alarm.markAsSent(); // 알람 전송 시 sent true로 변경
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(alarmResponse));
            } catch (IOException e) {
                emitters.remove(petId);
            }
        }
    }
}
