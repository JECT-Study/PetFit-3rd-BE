package ject.petfit.domain.slot.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.entry.repository.EntryRepository;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.routine.entity.Routine;
import ject.petfit.domain.routine.repository.RoutineRepository;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.exception.SlotErrorCode;
import ject.petfit.domain.slot.exception.SlotException;
import ject.petfit.domain.slot.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final PetRepository petRepository;
    private final SlotRepository slotRepository;
    private final RoutineRepository routineRepository;
    private final EntryRepository entryRepository;

    // ------------------------------ 슬롯 공통 메서드 -----------------------------------
    // 특정 반려동물의 슬롯 조회
//    private Slot getSlotOrThrow(Long petId) {
//        Pet pet = petRepository.findById(petId)
//                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
//        Slot slot = pet.getSlot();
//        if (slot == null) {
//            throw new SlotException(SlotErrorCode.SLOT_NOT_FOUND);
//        }
//        return slot;
//    }

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
        ;
    }

    // ------------------------------ API 메서드 -----------------------------------
    // @슬롯 초기화 (회원가입 슬롯 설정)
    @Transactional
    public SlotResponse initializePetSlot(Long petId, SlotInitializeRequest request) {
        // petId로 Pet 엔티티 조회
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        // 슬롯 레코드가 이미 존재하는지 확인
        if (slotRepository.existsByPetId(petId)) {
            throw new SlotException(SlotErrorCode.SLOT_ALREADY_EXISTS);
        }

        // 초기 슬롯 레코드 생성 및 저장
        Slot slot = createSlot(request, pet);
        return SlotResponse.from(slot);
    }

    // @슬롯 활성화 상태 조회
    public SlotResponse getSlotActivated(Long petId) {
        Slot slot = getSlotOrThrow(petId);
        return SlotResponse.from(slot);
    }

    // 슬롯 활성화 상태 설정
    @Transactional
    public SlotResponse setSlotActivated(Long petId, SlotRequest request) {
        Slot slot = getSlotOrThrow(petId);

        // 비활성화된 슬롯 리스트
        List<String> deActivatedList = new ArrayList<>();

        // 활성화 여부 업데이트
        Boolean feedActivated = request.getFeedActivated();
        if (feedActivated != null) {
            slot.updateFeedActivated(feedActivated);
            if (!feedActivated) {
                deActivatedList.add("feed");
            }
        }

        Boolean waterActivated = request.getWaterActivated();
        if (waterActivated != null) {
            slot.updateWaterActivated(waterActivated);
            if (!waterActivated) {
                deActivatedList.add("water");
            }
        }

        Boolean walkActivated = request.getWalkActivated();
        if (walkActivated != null) {
            slot.updateWalkActivated(walkActivated);
            if (!walkActivated) {
                deActivatedList.add("walk");
            }
        }

        Boolean dentalActivated = request.getDentalActivated();
        if (dentalActivated != null) {
            slot.updatePottyActivated(dentalActivated);
            if (!dentalActivated) {
                deActivatedList.add("potty");
            }
        }

        Boolean skinActivated = request.getSkinActivated();
        if (skinActivated != null) {
            slot.updateDentalActivated(skinActivated);
            if (!skinActivated) {
                deActivatedList.add("dental");
            }
        }

        Boolean pottyActivated = request.getPottyActivated();
        if (pottyActivated != null) {
            slot.updateSkinActivated(pottyActivated);
            if (!pottyActivated) {
                deActivatedList.add("skin");
            }
        }

        // 목표량
        if (request.getFeedAmount() != null) {
            slot.updateFeedAmount(request.getFeedAmount());
        }
        if (request.getWaterAmount() != null) {
            slot.updateWaterAmount(request.getWaterAmount());
        }
        if (request.getWalkAmount() != null) {
            slot.updateWalkAmount(request.getWalkAmount());
        }
        slotRepository.save(slot);

        /**
         * 비활성화로 슬롯을 변경했을때, 해당 루틴 CHECKED나 MEMO인 오늘의 루틴이 DB에 있다면 삭제
         */
        // 비활성화된 슬롯이 없다면 이대로 반환
        if (deActivatedList.isEmpty()) {
            return SlotResponse.from(slot);
        }

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        Optional<Entry> entry = entryRepository.findByPetAndEntryDate(pet, LocalDate.now());

        // 오늘의 entry가 없다면 이대로 반환
        if (entry.isEmpty()) {
            return SlotResponse.from(slot);
        }

        // 비활성화된 슬롯에 해당하는 루틴이 DB에 들어있다면 삭제
        for (String category : deActivatedList) {
            Optional<Routine> routine = routineRepository.findByEntryAndCategory(entry.get(), category);
            routine.ifPresent(routineRepository::delete);
        }

        return SlotResponse.from(slot);
    }

    public Slot createSlot(SlotInitializeRequest request, Pet pet) {
        return slotRepository.save(Slot.builder()
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
    }

}


