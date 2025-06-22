package ject.petfit.domain.schedule.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.schedule.dto.request.ScheduleRegisterRequest;
import ject.petfit.domain.schedule.dto.request.ScheduleUpdateRequest;
import ject.petfit.domain.schedule.dto.response.ScheduleResponse;
import ject.petfit.domain.schedule.entity.Schedule;
import ject.petfit.domain.schedule.exception.ScheduleErrorCode;
import ject.petfit.domain.schedule.exception.ScheduleException;
import ject.petfit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final EntryService entryService;
    private final EntryRepository entryRepository;

    // 홈화면 일정 조회(3일치)
    public List<ScheduleResponse> getHomeSchedule(Long petId, int selectDays) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 오늘부터 3일치 중에 일정이 있는 엔트리 리스트 조회 (is_scheduled = true)
        LocalDate today = LocalDate.now();
        List<Entry> entries
                = entryRepository.findAllByPetAndIsScheduledTrueAndEntryDateBetween(pet, today, today.plusDays(selectDays - 1));

        // 일정 날짜 오름차순 반환
        return entries.stream()
                .flatMap(entry -> entry.getSchedules().stream())
                .sorted(Comparator.comparing(Schedule::getTargetDate))
                .map(ScheduleResponse::from)
                .toList();
    }

    // 모든 일정 조회(all)
    public List<ScheduleResponse> getScheduleList(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 일정이 있는 엔트리 모두 조회
        List<Entry> entries = entryRepository.findAllByPetAndIsScheduledTrue(pet);

        // 일정 날짜 오름차순으로 반환
        return entries.stream()
                .flatMap(entry -> entry.getSchedules().stream())
                .sorted(Comparator.comparing(Schedule::getTargetDate))
                .map(ScheduleResponse::from)
                .toList();
    }

    // 추가 가능성
    // 일정 날짜 조회(날짜 요청, 리스트 응답)
    // 일정 ID 조회(ID 요청, 단일 응답)

    // 일정 등록
    @Transactional
    public ScheduleResponse createSchedule(Long petId, ScheduleRegisterRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        LocalDate targetDate = LocalDate.parse(request.getTargetDate());

        // (펫ID & 날짜)의 entry가 있으면 반환, 없으면 생성해서 반환
        Entry entry = entryService.getOrCreateEntry(pet, targetDate);

        // 일정 등록여부 true로 변경
        entry.updateScheduledTrue();

        // 일정 등록
        LocalDateTime targetDateTime = targetDate.atStartOfDay();
        Schedule schedule = scheduleRepository.save(
                Schedule.builder()
                        .entry(entry)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .targetDate(targetDateTime)
                        .build());
        return ScheduleResponse.from(schedule);
    }

    // 일정 수정
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        String requestTitle = request.getTitle();
        String requestContent = request.getContent();

        // 제목이나 내용 수정
        if (requestTitle != null && !requestTitle.isEmpty()) {
            schedule.updateTitle(requestTitle);
        }
        if (requestContent != null && !requestContent.isEmpty()) {
            schedule.updateContent(requestContent);
        }

        // 수정된 일정 저장
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return ScheduleResponse.from(updatedSchedule);
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        // 일정 삭제
        scheduleRepository.delete(schedule);

        // 일정 삭제 후 해당 일정의 entry가 더 이상 일정이 없으면 scheduled false로 변경
        Entry entry = schedule.getEntry();
        if (scheduleRepository.countByEntry(entry) == 0) {
            entry.updateScheduledFalse();
            entryRepository.save(entry);
        }
    }
}
