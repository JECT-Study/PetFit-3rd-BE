package ject.petfit.domain.schedule.facade;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.schedule.dto.request.ScheduleRegisterRequest;
import ject.petfit.domain.schedule.dto.request.ScheduleUpdateRequest;
import ject.petfit.domain.schedule.dto.response.ScheduleResponse;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.schedule.repository.ScheduleRepository;
import ject.petfit.domain.schedule.service.ScheduleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleFacade {
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final EntryService entryService;
    private final EntryRepository entryRepository;
    private final EntryQueryService entryQueryService;
    private final PetQueryService petQueryService;
    private final ScheduleQueryService scheduleQueryService;

    // 모든 일정 조회(all)
    public List<ScheduleResponse> getScheduleList(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);

        // 일정이 있는 엔트리 모두 조회
        List<Entry> entries = entryRepository.findAllByPetAndIsScheduledTrue(pet);

        // 일정 날짜 오름차순으로 반환
        return entries.stream()
                .flatMap(entry -> entry.getSchedules().stream())
                .sorted(Comparator.comparing(Schedule::getTargetDate))
                .map(ScheduleResponse::from)
                .toList();
    }

    // 홈화면 일정 조회(3일치)
    public List<ScheduleResponse> getHomeSchedule(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        List<Entry> entries = entryQueryService.getRecentEntriesWithSchedule(pet);

        // 일정 날짜 오름차순 반환
        return entries.stream()
                .flatMap(entry -> entry.getSchedules().stream())
                .sorted(Comparator.comparing(Schedule::getTargetDate))
                .map(ScheduleResponse::from)
                .toList();
    }

    @Transactional
    public ScheduleResponse createSchedule(Long petId, ScheduleRegisterRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        LocalDate targetDate = request.getTargetDate();

        // (펫ID & 날짜)의 entry가 있으면 반환, 없으면 생성해서 반환
        Entry entry = entryService.getOrCreateEntry(pet, targetDate);

        // 일정 등록
        Schedule schedule = scheduleRepository.save(
                Schedule.builder()
                        .entry(entry)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .targetDate(targetDate.atStartOfDay())
                        .build());

        // 일정 등록여부 true로 변경
        entry.updateScheduledTrue();

        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleQueryService.getScheduleOrThrow(scheduleId);
        String requestTitle = request.getTitle();
        String requestContent = request.getContent();

        // 제목이나 내용 수정
        if( requestTitle != null && !requestTitle.isEmpty()) {
            schedule.updateTitle(requestTitle);
        }
        if (requestContent != null && !requestContent.isEmpty()) {
            schedule.updateContent(requestContent);
        }
        schedule.updateTargetDate(request.getTargetDate().atStartOfDay());

        // 수정된 일정 저장
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return ScheduleResponse.from(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleQueryService.getScheduleOrThrow(scheduleId);

        // 일정 삭제
        scheduleRepository.delete(schedule);

        // 해당 entry의 일정 기록이 없다면 일정 등록 여부를 false로 변경
        Entry entry = schedule.getEntry();
        if (scheduleRepository.countByEntry(entry) == 0) {
            entry.updateScheduledFalse();
            entryRepository.save(entry);
        }
    }
}
