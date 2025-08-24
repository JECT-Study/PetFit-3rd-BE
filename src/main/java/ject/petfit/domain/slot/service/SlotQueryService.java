package ject.petfit.domain.slot.service;

import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.entity.SlotHistory;
import ject.petfit.domain.slot.exception.SlotErrorCode;
import ject.petfit.domain.slot.exception.SlotException;
import ject.petfit.domain.slot.repository.SlotHistoryRepository;
import ject.petfit.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SlotQueryService {
    private final SlotRepository slotRepository;
    private final SlotHistoryRepository slotHistoryRepository;

    public Optional<SlotHistory> getSlotHistoryOptional(Pet pet, LocalDate localDate) {
        return slotHistoryRepository.findSlotHistoryByPetAndRecordDate(pet, localDate);
    }

    public int countSlotHistoriesByDate(LocalDate localDate) {
        return slotHistoryRepository.countByRecordDate(localDate);
    }

    public Optional<Slot> getSlotOptional(Pet pet) {
        return slotRepository.findByPet(pet);
    }

    public Slot getSlotOrThrow(Pet pet) {
        return slotRepository.findByPet(pet)
                .orElseThrow(() -> new SlotException(SlotErrorCode.SLOT_NOT_FOUND));
    }

    // 활성화된 슬롯 카테고리 리스트 조회
    public List<String> getActivatedSlotCategories(Slot slot) {
        List<String> activatedCategories = new ArrayList<>();
        if (slot.isFeedActivated()) {
            activatedCategories.add("feed");
        }
        if (slot.isWaterActivated()) {
            activatedCategories.add("water");
        }
        if (slot.isWalkActivated()) {
            activatedCategories.add("walk");
        }
        if (slot.isPottyActivated()) {
            activatedCategories.add("potty");
        }
        if (slot.isDentalActivated()) {
            activatedCategories.add("dental");
        }
        if (slot.isSkinActivated()) {
            activatedCategories.add("skin");
        }
        return activatedCategories;
    }

    // 슬롯 목표량 반환
    public Integer getTargetAmountOrNull(Slot slot, String category) {
        return switch (category) {
            case "feed" -> slot.getFeedAmount();
            case "water" -> slot.getWaterAmount();
            case "walk" -> slot.getWalkAmount();
            case "potty", "skin", "dental" -> null;
            default -> throw new SlotException(SlotErrorCode.SLOT_CATEGORY_NOT_FOUND);
        };
    }

    // 펫의 슬롯이 존재하는지 검증
    public void validateSlotExists(Pet pet){
        if(slotRepository.existsByPet(pet)){
            throw new SlotException(SlotErrorCode.SLOT_ALREADY_EXISTS);
        }
    }

    // 슬롯에서 해당 카테고리가 활성화되어 있는지 검증
    public void validateSlotCategoryActivated(Slot slot, String category) {
        switch (category) {
            case "feed" -> {
                if (!slot.isFeedActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            case "water" -> {
                if (!slot.isWaterActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            case "walk" -> {
                if (!slot.isWalkActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            case "potty" -> {
                if (!slot.isPottyActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            case "dental" -> {
                if (!slot.isDentalActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            case "skin" -> {
                if (!slot.isSkinActivated()) {
                    throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
                }
            }
            default -> throw new SlotException(SlotErrorCode.SLOT_NOT_ACTIVATED);
        }
    }
}
