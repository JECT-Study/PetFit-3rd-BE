package ject.petfit.domain.alarm.service;

import ject.petfit.domain.alarm.dto.response.AlarmResponse;
import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.alarm.exception.AlarmErrorCode;
import ject.petfit.domain.alarm.exception.AlarmException;
import ject.petfit.domain.alarm.repository.AlarmRepository;
import ject.petfit.domain.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmQueryService {
    private final AlarmRepository alarmRepository;

    public List<AlarmResponse> getAllUnreadAlarms(Pet pet) {
//        LocalDateTime oneMinuteAgo = LocalDateTime.now().withSecond(0).withNano(0).minusMinutes(1);
//        List<Alarm> unreadAlarms = alarmRepository.findByIsReadFalseAndPetAndTargetDateTimeBeforeOrderByTargetDateTimeDesc(pet, oneMinuteAgo);
        List<Alarm> unreadAlarms = alarmRepository.findByIsReadFalseAndPetOrderByTargetDateTimeDesc(pet);
        return unreadAlarms.stream()
                .map(AlarmResponse::from)
                .toList();
    }

    public List<Alarm> getAlarmsWithinNextThreeDays(Pet pet) {
        LocalDateTime nowParsed = LocalDate.now().atStartOfDay();
        return alarmRepository.findAllByPetAndTargetDateTimeBetween(
                pet,
                nowParsed,
                nowParsed.plusDays(3)
        );
    }

    public Alarm getAlarmOrThrow(Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));
    }




}
