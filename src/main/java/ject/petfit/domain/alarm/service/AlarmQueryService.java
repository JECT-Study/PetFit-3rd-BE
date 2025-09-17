package ject.petfit.domain.alarm.service;

import ject.petfit.domain.alarm.dto.response.AlarmResponse;
import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.alarm.repository.AlarmRepository;
import ject.petfit.domain.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmQueryService {
    private final AlarmRepository alarmRepository;

    public List<AlarmResponse> getUnreadAlarms(Pet pet) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().withSecond(0).withNano(0).minusMinutes(1);
        List<Alarm> unreadAlarms = alarmRepository.findByIsReadFalseAndPetAndTargetDateTimeBeforeOrderByTargetDateTimeDesc(pet, oneMinuteAgo);
        return unreadAlarms.stream()
                .map(AlarmResponse::from)
                .toList();
    }

//    getHomeAlarmList

}
