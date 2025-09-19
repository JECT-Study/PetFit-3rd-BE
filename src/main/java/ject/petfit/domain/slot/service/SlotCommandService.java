package ject.petfit.domain.slot.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.entity.SlotHistory;
import ject.petfit.domain.slot.repository.SlotHistoryRepository;
import ject.petfit.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;
    private final SlotHistoryRepository slotHistoryRepository;

    public SlotHistory saveTodaySlotHistory(Slot slot, LocalDate todayDate) {
        return slotHistoryRepository.save(SlotHistory.builder()
                .pet(slot.getPet())
                .recordDate(todayDate)
                .feedActivated(slot.isFeedActivated())
                .waterActivated(slot.isWaterActivated())
                .walkActivated(slot.isWalkActivated())
                .pottyActivated(slot.isPottyActivated())
                .dentalActivated(slot.isDentalActivated())
                .skinActivated(slot.isSkinActivated())
                        // 새로 추가
                        .supplementActivated(slot.getSupplementActivated())
                        .supplementAmount(slot.getSupplementAmount())
                        .medicineActivated(slot.getMedicineActivated())
                        .medicineAmount(slot.getMedicineAmount())
                        .custom1Activated(slot.getCustom1Activated())
                        .custom1Name(slot.getCustom1Name())
                        .custom1Content(slot.getCustom1Content())
                        .custom2Activated(slot.getCustom2Activated())
                        .custom2Name(slot.getCustom2Name())
                        .custom2Content(slot.getCustom2Content())
                        .custom3Activated(slot.getCustom3Activated())
                        .custom3Name(slot.getCustom3Name())
                        .custom3Content(slot.getCustom3Content())
                .feedAmount(slot.getFeedAmount())
                .waterAmount(slot.getWaterAmount())
                .walkAmount(slot.getWalkAmount())
                .build());
    }

    public Slot createSlot(Pet pet, SlotInitializeRequest request) {
        return slotRepository.save(Slot.builder()
                .pet(pet)
                .feedActivated(request.isFeedActivated())
                .waterActivated(request.isWaterActivated())
                .walkActivated(request.isWalkActivated())
                .pottyActivated(request.isPottyActivated())
                .dentalActivated(request.getDentalActivated())
                .skinActivated(request.getSkinActivated())
                .feedAmount(request.getFeedAmount())
                .waterAmount(request.getWaterAmount())
                .walkAmount(request.getWalkAmount())
                    // 새로 추가
                    .supplementActivated(request.getSupplementActivated())
                    .supplementAmount(request.getSupplementAmount())
                    .medicineActivated(request.getMedicineActivated())
                    .medicineAmount(request.getMedicineAmount())
                    .custom1Activated(request.getCustom1Activated())
                    .custom1Name(request.getCustom1Name())
                    .custom1Content(request.getCustom1Content())
                    .custom2Activated(request.getCustom2Activated())
                    .custom2Name(request.getCustom2Name())
                    .custom2Content(request.getCustom2Content())
                    .custom3Activated(request.getCustom3Activated())
                    .custom3Name(request.getCustom3Name())
                    .custom3Content(request.getCustom3Content())
                .build());
    }

    // 슬롯 옵션 업데이트 및 비활성화 카테고리들 반환
    public List<String> updateSlot(Slot slot, SlotRequest request) {
        List<String> deActivatedList = new ArrayList<>();

        slot.updateSlot(request);

        // 비활성화된 카테고리들 확인
        if (!slot.isFeedActivated()) {
            deActivatedList.add("feed");
        }
        if (!slot.isWaterActivated()) {
            deActivatedList.add("water");
        }
        if (!slot.isWalkActivated()) {
            deActivatedList.add("walk");
        }
        if (!slot.isPottyActivated()) {
            deActivatedList.add("potty");
        }
        if (!slot.isDentalActivated()) {
            deActivatedList.add("dental");
        }
        if (!slot.isSkinActivated()) {
            deActivatedList.add("skin");
        }
        if (slot.getSupplementActivated() != null && !slot.getSupplementActivated()) {
            deActivatedList.add("supplement");
        }
        if (slot.getMedicineActivated() != null && !slot.getMedicineActivated()) {
            deActivatedList.add("medicine");
        }
        if (slot.getCustom1Activated() != null && !slot.getCustom1Activated()) {
            deActivatedList.add("custom1");
        }
        if (slot.getCustom2Activated() != null && !slot.getCustom2Activated()) {
            deActivatedList.add("custom2");
        }
        if (slot.getCustom3Activated() != null && !slot.getCustom3Activated()) {
            deActivatedList.add("custom3");
        }
        return deActivatedList;
    }

}
