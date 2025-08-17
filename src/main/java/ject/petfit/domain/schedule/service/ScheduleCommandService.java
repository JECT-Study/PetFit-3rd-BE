package ject.petfit.domain.schedule.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class ScheduleCommandService {
    private final ScheduleRepository scheduleRepository;

    public Schedule createSchedule(Entry entry, String title, String content, LocalDateTime localDateTime) {
        return scheduleRepository.save(
                Schedule.builder()
                        .entry(entry)
                        .title(title)
                        .content(content)
                        .targetDate(localDateTime)
                        .build());

    }

    public Schedule updateSchedule(Schedule schedule, String title, String content, LocalDateTime localDateTime) {
        if (!title.isEmpty()) {
            schedule.updateTitle(title);
        }
        if (content != null && !content.isEmpty()) {
            schedule.updateContent(content);
        }
        schedule.updateTargetDate(localDateTime);
        return schedule;
    }

    public void deleteSchedule(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }
}
