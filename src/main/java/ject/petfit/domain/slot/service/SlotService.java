package ject.petfit.domain.slot.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.exception.SlotErrorCode;
import ject.petfit.domain.slot.exception.SlotException;
import ject.petfit.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final PetRepository petRepository;
    private final SlotRepository slotRepository;

    // ------------------------------ 슬롯 공통 메서드 -----------------------------------
    // 특정 반려동물의 슬롯 조회
    private Slot getSlotOrThrow(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Slot slot = pet.getSlot();
        if (slot == null) {
            throw new SlotException(SlotErrorCode.SLOT_NOT_FOUND);
        }
        return slot;
    }

    // 슬롯 활성화된 옵션명 리스트 조회
    public List<String> getActivatedSlotOptions(Slot slot) {
        List<String> activatedOptions = new ArrayList<>();
        if(slot.isFeedActivated()){
            activatedOptions.add("feed");
        }
        if(slot.isWaterActivated()){
            activatedOptions.add("water");
        }
        if(slot.isWalkActivated()){
            activatedOptions.add("walk");
        }
        if(slot.isPottyActivated()){
            activatedOptions.add("potty");
        }
        if(slot.isDentalActivated()){
            activatedOptions.add("dental");
        }
        if(slot.isSkinActivated()){
            activatedOptions.add("skin");
        }
        return activatedOptions;
    }

    // 해당 카테고리가 슬롯에서 활성화되어 있는지 확인
    public void isCategorySlotActivated(Slot slot, String category) {
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
        };
    }

    // ------------------------------ API 메서드 -----------------------------------
    // 슬롯 초기화 (회원가입 슬롯 설정)
    @Transactional
    public SlotResponse initializePetSlot(Long petId, SlotInitializeRequest request) {
        // petId로 Pet 엔티티 조회
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 슬롯 레코드가 이미 존재하는지 확인
        if (slotRepository.existsByPetId(petId)) {
            throw new SlotException(SlotErrorCode.SLOT_ALREADY_EXISTS);
        }

        // 초기 슬롯 레코드 생성 및 저장
        Slot slot = slotRepository.save(Slot.builder()
                .feedActivated(request.isFeedActivated())
                .waterActivated(request.isWaterActivated())
                .walkActivated(request.isWalkActivated())
                .pottyActivated(request.isPottyActivated())
                .dentalActivated(request.isDentalActivated())
                .skinActivated(request.isSkinActivated())
                .feedAmount(request.getFeedAmount())
                .waterAmount(request.getWaterAmount())
                .walkAmount(request.getWalkAmount())
                .pet(pet)
                .build());
        return SlotResponse.from(slot);
    }


    // 슬롯 활성화 상태 조회
    public SlotResponse getSlotActivated(Long petId) {
        Slot slot = getSlotOrThrow(petId);
        return SlotResponse.from(slot);
    }

    // 슬롯 활성화 상태 설정
    @Transactional
    public SlotResponse setSlotActivated(Long petId, SlotRequest request) {
        Slot slot = getSlotOrThrow(petId);
        slot.updateSlot(request);
        slotRepository.save(slot);
        return SlotResponse.from(slot);
    }

//    // 사료, 음수, 배변 목표량 조회
//    public SlotAmountResponse getSlotAmounts(Long petId) {
//        Slot slot = getSlotOrThrow(petId);
//        return SlotAmountResponse.from(slot);
//    }
//
//    // 사료, 음수, 배변 목표량 설정
//    @Transactional
//    public SlotAmountResponse setSlotAmounts(Long petId, @Valid SlotAmountRequest request) {
//        Slot slot = getSlotOrThrow(petId);
//
//        // 사료, 음수, 산책 목표량 업데이트 (null 값은 수정하지 않음)
//        if (request.getFeedAmount() != null) {
//            slot.updateFeedAmount(request.getFeedAmount());
//        }
//        if (request.getWaterAmount() != null) {
//            slot.updateWaterAmount(request.getWaterAmount());
//        }
//        if (request.getWalkAmount() != null) {
//            slot.updateWalkAmount(request.getWalkAmount());
//        }
//
//        slotRepository.save(slot);
//        return SlotAmountResponse.from(slot);
//    }
}
