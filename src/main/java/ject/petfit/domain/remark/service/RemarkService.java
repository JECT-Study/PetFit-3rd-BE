package ject.petfit.domain.remark.service;

import ject.petfit.domain.entry.service.EntryService;
import ject.petfit.domain.remark.dto.response.RemarkResponse;
import ject.petfit.domain.remark.exception.RemarkErrorCode;
import ject.petfit.domain.remark.exception.RemarkException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.remark.dto.request.RemarkRegisterRequest;
import ject.petfit.domain.remark.dto.request.RemarkUpdateRequest;
import ject.petfit.domain.remark.entity.Remark;
import ject.petfit.domain.remark.repository.RemarkRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
@Service
public class RemarkService {
    private final RemarkRepository remarkRepository;
    private final PetRepository petRepository;
    private final EntryRepository entryRepository;
    private final EntryService entryService;

    // 홈화면 특이사항 조회(3일치)
    public List<RemarkResponse> getHomeRemark(Long petId, int selectDays) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 오늘부터 3일치 중에 특이사항이 있는 엔트리 리스트 조회
        LocalDate today = LocalDate.now();
        List<Entry> entries
                = entryRepository.findAllByPetAndIsRemarkedTrueAndEntryDateBetween(pet, today, today.plusDays(selectDays - 1));

        // 특이사항 날짜 오름차순 반환
        return entries.stream()
                .flatMap(entry -> entry.getRemarks().stream())
                .sorted(comparing(Remark::getRemarkDate))
                .map(RemarkResponse::from)
                .toList();// 특이사항이 있는 엔트리에서 특이사항 내용만 추출하여 반환
    }

    // 추가 가능성
    // 특이사항 날짜 조회(날짜 요청, 리스트 응답)
    // 특이사항 ID 조회(ID 요청, 단일 응답)

    // 특이사항 등록
    // 현재는 '일'까지만 요청, 추후 '시분초'까지 요청 가능하도록 변경할지 검토 필요
    @Transactional
    public RemarkResponse createRemark(Long petId, RemarkRegisterRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        LocalDate remarkDate = LocalDate.parse(request.getRemarkDate());

        // (펫ID & 날짜)의 entry가 있으면 반환, 없으면 생성해서 반환
        Entry entry = entryService.getOrCreateEntry(pet, remarkDate);

        // 특이사항 등록여부 true로 변경
        entry.updateRemarkedTrue();

        // 특이사항 등록
        LocalDateTime targetDateTime = remarkDate.atStartOfDay();
        Remark remark = remarkRepository.save(
                Remark.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .remarkDate(targetDateTime)
                        .entry(entry)
                        .build());
        return RemarkResponse.from(remark);
    }

    // 특이사항 수정
    @Transactional
    public RemarkResponse updateRemark(Long remarkId, RemarkUpdateRequest request) {
        Remark remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RemarkException(RemarkErrorCode.REMARK_NOT_FOUND));
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

    // 특이사항 삭제
    @Transactional
    public void deleteRemark(Long remarkId) {
        Remark remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RemarkException(RemarkErrorCode.REMARK_NOT_FOUND));

        // 특이사항 삭제
        remarkRepository.delete(remark);

        // 특이사항 삭제 후 해당 entry의 특이사항 등록 여부를 false로 변경
        Entry entry = remark.getEntry();
        if (remarkRepository.countByEntry(entry) == 0) {
            entry.updateScheduledFalse();
            entryRepository.save(entry);
        }
    }
}
