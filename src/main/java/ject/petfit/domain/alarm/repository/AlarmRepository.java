package ject.petfit.domain.alarm.repository;

import ject.petfit.domain.alarm.entity.Alarm;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    // 현재 시각 알람 조회용
    List<Alarm> findByTargetDateTimeAndIsReadFalse(LocalDateTime targetDateTime);

//    List<Alarm> findByIsReadFalseAndTargetDateTimeBefore(LocalDateTime oneMinuteAgo);
//
//    List<Alarm> findByIsReadFalseAndTargetDateTimeBeforeOrderByTargetDateTimeDesc(LocalDateTime oneMinuteAgo);
//
//    List<Alarm> findByIsReadFalseAndUserIdAndTargetDateTimeBeforeOrderByTargetDateTimeDesc(long userId, LocalDateTime oneMinuteAgo);

    List<Alarm> findByIsReadFalseAndPetAndTargetDateTimeBeforeOrderByTargetDateTimeDesc(Pet pet, LocalDateTime oneMinuteAgo);
    List<Alarm> findByPetAndIsReadFalseAndTargetDateTimeBefore(Pet pet, LocalDateTime nowParsed);
}
