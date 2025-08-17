package ject.petfit.domain.slot.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;

    public Slot createSlot(Pet pet, SlotInitializeRequest request) {
        return slotRepository.save(Slot.builder()
                .pet(pet)
                .feedActivated(request.isFeedActivated())
                .waterActivated(request.isWaterActivated())
                .walkActivated(request.isWalkActivated())
                .pottyActivated(request.isPottyActivated())
                .dentalActivated(request.isDentalActivated())
                .skinActivated(request.isSkinActivated())
                .feedAmount(request.getFeedAmount())
                .waterAmount(request.getWaterAmount())
                .walkAmount(request.getWalkAmount())
                .build());
    }

    // 슬롯 옵션 업데이트 및 비활성화 카테고리들 반환
    public List<String> updateSlot(Slot slot, SlotRequest request) {
        List<String> deActivatedList = new ArrayList<>();

        slot.updateSlot(request);
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

        return deActivatedList;
    }

}
