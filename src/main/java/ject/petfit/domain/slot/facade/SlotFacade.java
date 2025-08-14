package ject.petfit.domain.slot.facade;

import jakarta.transaction.Transactional;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.service.PetQueryService;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.entity.Slot;
import ject.petfit.domain.slot.exception.SlotErrorCode;
import ject.petfit.domain.slot.exception.SlotException;
import ject.petfit.domain.slot.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotFacade {
    private final SlotService slotService;
    private final PetQueryService petQueryService;

    // 슬롯 초기화 (회원가입 슬롯 설정)
    @Transactional
    public SlotResponse initializePetSlot(Long petId, SlotInitializeRequest request) {
        Pet pet = petQueryService.getPetOrThrow(petId);

        // 슬롯 레코드가 이미 존재하는지 검증
        slotService.validateSlotExists(pet);

        // 초기 슬롯 레코드 저장 및 반환
        return SlotResponse.from(slotService.createSlot(request, pet));
    }

    // 슬롯 활성화 상태 조회
    public SlotResponse getSlotActivated(Long petId) {
        Pet pet = petQueryService.getPetOrThrow(petId);
        return SlotResponse.from( slotService.getSlotOrThrow(pet));
    }
}
