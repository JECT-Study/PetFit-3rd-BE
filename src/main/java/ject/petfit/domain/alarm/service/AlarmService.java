//package ject.petfit.domain.alarm.service;
//
//import jakarta.transaction.Transactional;
//import ject.petfit.domain.alarm.dto.request.AlarmRegisterRequest;
//import ject.petfit.domain.alarm.dto.response.AlarmResponse;
//import ject.petfit.domain.alarm.entity.Alarm;
//import ject.petfit.domain.alarm.repository.AlarmRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AlarmService {
//    private final AlarmRepository alarmRepository;
//
//    @Transactional
//    public void saveAlarm(AlarmRegisterRequest request, String token) {
//        Alarm alarm = Alarm.builder()
//                .title(request.getTitle())
//                .content(request.getContent())
//                .targetDateTime(request.getTargetDateTime())
//                .isRead(false)
//                .isSent(false)
//                .userId(Long.parseLong(token))
//                .build();
//        log.info("Saving alarm: {}", alarm.getTitle());
//        alarmRepository.save(alarm);
//    }
//
//    @Transactional
//    public void markAsRead(Long alarmId) {
//        alarmRepository.findById(alarmId).ifPresent(alarm -> {
//            alarm.markAsRead();
//            alarmRepository.save(alarm);
//            log.info("Alarm {} marked as read", alarmId);
//        });
//    }
//
//    @Transactional
//    public void markAllAsRead() {
//        LocalDateTime nowParsed = LocalDateTime.now().withSecond(0).withNano(0);
//        // 현재 시각 이전 조회이므로 1분전부터 포함
//        alarmRepository.findByIsReadFalseAndTargetDateTimeBefore(nowParsed).forEach(alarm -> {
//            alarm.markAsRead();
//            alarmRepository.save(alarm);
//        });
//    }
//}
