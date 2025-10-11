package ject.petfit.domain.alarm.facade;

import jakarta.transaction.Transactional;
import ject.petfit.domain.alarm.dto.request.AlarmRegisterRequest;
import ject.petfit.domain.alarm.dto.request.AlarmUpdateRequest;
import ject.petfit.domain.alarm.dto.response.AlarmResponse;
import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.alarm.service.AlarmCommandService;
import ject.petfit.domain.alarm.service.AlarmQueryService;
import ject.petfit.domain.alarm.service.SseEmitterService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmFacade {
    private final SseEmitterService sseEmitterService;
    private final AlarmCommandService alarmCommandService;
    private final AlarmQueryService alarmQueryService;
    private final PetQueryService petQueryService;

    public SseEmitter createEmitter(Long petId) {
        return sseEmitterService.createEmitter(petId);
    }

    @Transactional
    public void saveAlarm(AlarmRegisterRequest request, Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        alarmCommandService.saveAlarm(request, pet);
    }

    public List<AlarmResponse> getAllAlarms(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        return alarmQueryService.getAllAlarms(pet);
    }

    public List<AlarmResponse> getAllUnreadAlarms(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        return alarmQueryService.getAllUnreadAlarms(pet);
    }

    @Transactional
    public void deleteAlarm(Long alarmId) {
        alarmCommandService.deleteAlarm(alarmId);
    }

    @Transactional
    public void markAsRead(Long alarmId) {
        alarmCommandService.markAsRead(alarmId);
    }

    @Transactional
    public void markAllAsRead(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        alarmCommandService.markAllAsRead(pet);
    }

    public List<AlarmResponse> getHomeAlarms(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        List<Alarm> alarmList = alarmQueryService.getAlarmsWithinNextThreeDays(pet);
        return alarmList.stream()
                .map(AlarmResponse::from)
                .toList();
    }

    @Transactional
    public AlarmResponse updateAlarm(Long alarmId, AlarmUpdateRequest alarmUpdateRequest) {
        Alarm alarm = alarmQueryService.getAlarmOrThrow(alarmId);
        return alarmCommandService.updateAlarm(alarm, alarmUpdateRequest);
    }

}
