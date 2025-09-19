package ject.petfit.domain.slot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.request.SlotRequest;
import ject.petfit.domain.slot.dto.response.SlotResponse;
import ject.petfit.domain.slot.facade.SlotFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slots")
@Tag(name = "Slot", description = "슬롯 API")
public class SlotController {
    private final SlotFacade slotFacade;

    // 슬롯 초기화 (회원가입 후 슬롯 설정)
    @PostMapping("/{petId}")
    @Operation(summary = "슬롯 초기화 (회원가입 슬롯 설정)", description = "회원가입 후 반려동물의 슬롯을 초기화 <br>" +
            "사료량, 음수량, 산책량은 미설정시 null 입력 <br>" +
            "기존 슬롯들의 활성화 여부는 null 입력 불가 <br>" +
            "없어질 (치아, 피부) 슬롯과 새로 추가된 영양제, 약, 커스텀 슬롯은 null 입력 가능")
    public ResponseEntity<ApiResponse<SlotResponse>> initialize(
            @PathVariable Long petId,
            @RequestBody @Valid SlotInitializeRequest slotInitializeRequest
            ) {
        return ResponseEntity.status(201).body(ApiResponse.success(
                slotFacade.initializePetSlot(petId, slotInitializeRequest))
        );
    }

    // 슬롯 활성화 상태 조회
    @GetMapping("/{petId}")
    @Operation(summary = "슬롯 설정 조회", description = "특정 반려동물의 슬롯 설정을 조회")
    public ResponseEntity<ApiResponse<SlotResponse>> activated(
            @PathVariable Long petId)
    {
        return ResponseEntity.ok(ApiResponse.success(slotFacade.getSlotActivated(petId)));
    }

    // 슬롯 활성화 상태 변경
    @PatchMapping("/{petId}")
    @Operation(summary = "슬롯 설정 변경", description = "특정 반려동물의 슬롯 설정을 변경 <br>" +
            "수정 없는 슬롯은 null 입력 혹은 기존값 입력")
    public ResponseEntity<ApiResponse<SlotResponse>> activated(
            @PathVariable Long petId,
            @RequestBody @Valid SlotRequest slotRequest
    ){
        return ResponseEntity.status(201).body(
                ApiResponse.success(slotFacade.setSlotActivated(petId, slotRequest))
        );
    }
}
