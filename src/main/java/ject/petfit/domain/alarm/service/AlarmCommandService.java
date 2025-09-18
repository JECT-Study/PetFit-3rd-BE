package ject.petfit.domain.alarm.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.alarm.dto.request.AlarmRegisterRequest;
import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.alarm.repository.AlarmRepository;
import ject.petfit.domain.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCommandService {
    private final AlarmRepository alarmRepository;

    public void saveAlarm(AlarmRegisterRequest request, Pet pet) {
        alarmRepository.save(Alarm.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetDateTime(request.getTargetDateTime())
                .isRead(false)
                .isSent(false)
                .pet(pet)
                .build());
    }

    public void markAsRead(Long alarmId) {
        alarmRepository.findById(alarmId).ifPresent(alarm -> {
            alarm.markAsRead();
            alarmRepository.save(alarm);
        });
    }

    public void markAllAsRead(Pet pet) {
        LocalDateTime nowParsed = LocalDateTime.now().withSecond(0).withNano(0);
        // 현재 시각 이전 조회이므로 1분전부터 포함
        alarmRepository.findByPetAndIsReadFalseAndTargetDateTimeBefore(pet, nowParsed).forEach(alarm -> {
            alarm.markAsRead();
            alarmRepository.save(alarm);
        });
    }
}
