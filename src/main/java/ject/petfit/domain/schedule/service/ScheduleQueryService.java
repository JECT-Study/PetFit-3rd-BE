package ject.petfit.domain.schedule.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.schedule.exception.ScheduleErrorCode;
import ject.petfit.domain.schedule.exception.ScheduleException;
import ject.petfit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;

    public Schedule getScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    public long countByEntry(Entry entry) {
        return scheduleRepository.countByEntry(entry);
    }
}
