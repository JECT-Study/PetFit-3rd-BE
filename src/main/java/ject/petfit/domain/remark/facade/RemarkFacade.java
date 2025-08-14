package ject.petfit.domain.remark.facade;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.entry.service.EntryQueryService;
import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.remark.dto.request.RemarkRegisterRequest;
import ject.petfit.domain.remark.dto.request.RemarkUpdateRequest;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import ject.petfit.domain.remark.entity.Remark;
import ject.petfit.domain.remark.repository.RemarkRepository;
import ject.petfit.domain.remark.service.RemarkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class RemarkFacade {
    private final PetQueryService petQueryService;
    private final RemarkQueryService remarkQueryService;
    private final RemarkRepository remarkRepository;
    private final EntryService entryService;
    private final EntryQueryService entryQueryService;
    private final EntryRepository entryRepository;

    // 홈화면 특이사항 조회(3일치)
    public List<RemarkResponse> getHomeRemark(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        List<Entry> entries = entryQueryService.getRecentEntriesWithRemark(pet);

        // 특이사항 날짜 오름차순 반환
        return entries.stream()
                .flatMap(entry -> entry.getRemarks().stream())
                .sorted(comparing(Remark::getRemarkDate))
                .map(RemarkResponse::from)
                .toList();// 특이사항이 있는 엔트리에서 특이사항 내용만 추출하여 반환
    }

    @Transactional
    public RemarkResponse createRemark(Long petId, RemarkRegisterRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        LocalDate remarkDate = request.getRemarkDate();

        // (펫ID & 날짜)의 entry가 있으면 반환, 없으면 생성해서 반환
        Entry entry = entryService.getOrCreateEntry(pet,remarkDate);


        // 특이사항 등록
        Remark remark = remarkRepository.save(
                Remark.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .remarkDate(remarkDate.atStartOfDay())
                        .entry(entry)
                        .build());

        // 특이사항 등록여부 true로 변경
        entry.updateRemarkedTrue();

        return RemarkResponse.from(remark);
    }

    @Transactional
    public RemarkResponse updateRemark(Long remarkId, RemarkUpdateRequest request) {
        Remark remark = remarkQueryService.getRemarkOrThrow(remarkId);
        String requestTitle = request.getTitle();
        String requestContent = request.getContent();

        // 제목이나 내용 수정
        if (requestTitle != null && !requestTitle.isEmpty()) {
            remark.updateTitle(requestTitle);
        }
        if (requestContent != null && !requestContent.isEmpty()) {
            remark.updateContent(requestContent);
        }

        // 수정된 특이사항 저장
        Remark updatedRemark = remarkRepository.save(remark);
        return RemarkResponse.from(updatedRemark);
    }

    @Transactional
    public void deleteRemark(Long remarkId) {
        Remark remark = remarkQueryService.getRemarkOrThrow(remarkId);

        // 특이사항 삭제
        remarkRepository.delete(remark);

        // 해당 entry의 특이사항 기록이 없다면 특이사항 등록 여부를 false로 변경
        Entry entry = remark.getEntry();
        if (remarkRepository.countByEntry(entry) == 0) {
            entry.updateScheduledFalse();
            entryRepository.save(entry);
        }
    }
}
