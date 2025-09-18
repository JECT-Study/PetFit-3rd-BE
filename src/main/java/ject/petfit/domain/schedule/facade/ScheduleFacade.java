package ject.petfit.domain.schedule.facade;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.entry.service.EntryCommandService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.schedule.dto.request.ScheduleRegisterRequest;
import ject.petfit.domain.schedule.dto.request.ScheduleUpdateRequest;
import ject.petfit.domain.schedule.dto.response.ScheduleResponse;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.schedule.service.ScheduleCommandService;
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
    private final PetQueryService petQueryService;
    private final EntryQueryService entryQueryService;
    private final EntryCommandService entryCommandService;
    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleCommandService scheduleCommandService;

    // 모든 일정 조회(all)
    public List<ScheduleResponse> getScheduleList(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);

        // 일정이 있는 엔트리 모두 조회
        List<Entry> entries = entryQueryService.getEntriesByPetAndScheduled(pet);

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
        Entry entry = entryCommandService.getOrCreateEntry(pet, targetDate);

        // 일정 등록
        Schedule schedule = scheduleCommandService.createSchedule(
                entry,
                request.getTitle(),
                request.getContent(),
                request.getTargetDate().atStartOfDay()
        );

        // 일정 등록여부 true로 변경
        if(!entry.getIsScheduled()) {
            entry.updateScheduledTrue();
        }

        return ScheduleResponse.from(schedule);
    }


    /**
     # 수정 날짜가 기존 날짜와 다르다면 다음을 수행 - 순서 유의
     1) 수정 날짜의 Entry getOrCreate
     2) Schedule의 외래키를 수정 날짜의 Entry로 변경
     3) 기존 날짜 Entry의 일정 등록 여부 업데이트 (일정 없으면 false로 변경)
     4) 수정 날짜 Entry의 일정 등록 여부 업데이트 (true)

     # 공통
     Schedule의 title, content, targetDate 수정
    */
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleQueryService.getScheduleOrThrow(scheduleId);
        LocalDate originDate = schedule.getEntry().getEntryDate();
        LocalDate modifyDate = request.getTargetDate();

        // 수정 날짜가 기존 날짜와 다르다면 다음을 수행
        if(!originDate.equals(modifyDate)) {
            Entry originEntry = schedule.getEntry(); // 기존 날짜의 Entry
            Pet pet = schedule.getEntry().getPet();

            // 1) 수정 날짜의 Entry getOrCreate
            Entry modifyEntry = entryCommandService.getOrCreateEntry(pet, modifyDate);
            // 2) Schedule의 외래키를 수정 날짜의 Entry로 변경
            schedule.updateEntry(modifyEntry);
            // 3) 기존 날짜 Entry의 일정 등록 여부 업데이트 (일정 없으면 false로 변경)
            if (scheduleQueryService.countByEntry(originEntry) == 0) {
                originEntry.updateScheduledFalse();
            }
            // 4) 수정 날짜 Entry의 일정 등록 여부 업데이트 (true)
            if(!modifyEntry.getIsScheduled()) {
                modifyEntry.updateScheduledTrue();
            }
        }

        // Schedule의 title, content, targetDate 수정
        Schedule updatedSchedule = scheduleCommandService.updateSchedule(
                schedule,
                request.getTitle(),
                request.getContent(),
                request.getTargetDate().atStartOfDay()
        );

        return ScheduleResponse.from(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleQueryService.getScheduleOrThrow(scheduleId);
        scheduleCommandService.deleteSchedule(schedule);

        // 해당 entry의 일정 기록이 없다면 일정 등록 여부를 false로 변경
        Entry entry = schedule.getEntry();
        if (scheduleQueryService.countByEntry(entry) == 0) {
            entry.updateScheduledFalse();
        }
    }


}
