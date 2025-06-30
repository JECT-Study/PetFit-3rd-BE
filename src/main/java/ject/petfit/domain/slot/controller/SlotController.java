package ject.petfit.domain.slot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ject.petfit.domain.slot.dto.request.SlotActivatedRequest;
import ject.petfit.domain.slot.dto.request.SlotAmountRequest;
import ject.petfit.domain.slot.dto.request.SlotInitializeRequest;
import ject.petfit.domain.slot.dto.response.SlotActivatedResponse;
import ject.petfit.domain.slot.dto.response.SlotAmountResponse;
import ject.petfit.domain.slot.dto.response.SlotInitializeResponse;
import ject.petfit.domain.slot.service.SlotService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slots")
@Tag(name = "Slot", description = "슬롯 설정 API")
public class SlotController {
    private final SlotService slotService;

    // 반려동물 슬롯 초기화 (회원가입 슬롯 설정)
    @PostMapping("/{petId}/initialize")
    @Operation(summary = "반려동물 슬롯 초기화", description = "회원가입 후 반려동물의 슬롯을 초기화합니다. <br>" +
            "사료량, 음수량, 배변량은 미설정시 null 입력 <br>" +
            "슬롯 6개 활성화 여부는 null 입력 불가")
    public ResponseEntity<ApiResponse<SlotInitializeResponse>> initialize(
            @PathVariable Long petId,
            @RequestBody @Valid SlotInitializeRequest slotInitializeRequest
            ) {

        return ResponseEntity.status(201).body(ApiResponse.success(
                slotService.initializePetSlot(petId, slotInitializeRequest))
        );
    }

    // 슬롯 활성화 상태 조회
    @GetMapping("/{petId}/activated")
    @Operation(summary = "슬롯 활성화 상태 조회", description = "특정 반려동물의 슬롯 활성화 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<SlotActivatedResponse>> activated(
            @PathVariable Long petId)
    {
        return ResponseEntity.ok(ApiResponse.success(slotService.getSlotActivated(petId)));
    }

    // 슬롯 활성화 상태 변경
    @PatchMapping("/{petId}/activated")
    @Operation(summary = "슬롯 활성화 상태 변경", description = "특정 반려동물의 슬롯 활성화 상태를 변경합니다. <br>" +
            "수정 없는 슬롯은 null 입력 혹은 기존값 입력")
    public ResponseEntity<ApiResponse<SlotActivatedResponse>> activated(
            @PathVariable Long petId,
            @RequestBody @Valid SlotActivatedRequest slotActivatedRequest
    ){
        return ResponseEntity.status(201).body(
                ApiResponse.success(slotService.setSlotActivated(petId, slotActivatedRequest))
        );
    }

    // 사료, 음수, 배변 목표량 조회
    @GetMapping("/{petId}/amounts")
    @Operation(summary = "사료, 음수, 배변 목표량 조회", description = "특정 반려동물의 사료, 음수, 배변 목표량을 조회합니다.")
    public ResponseEntity<ApiResponse<SlotAmountResponse>> getAmounts(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(ApiResponse.success(slotService.getSlotAmounts(petId)));
    }

    // 사료, 음수, 배변 목표량 설정
    @PatchMapping("/{petId}/amounts")
    @Operation(summary = "사료, 음수, 배변 목표량 설정", description = "특정 반려동물의 사료, 음수, 배변 목표량을 설정합니다.<br>" +
            "수정 없는 슬롯은 null 입력 혹은 기존값 입력")
    public ResponseEntity<ApiResponse<SlotAmountResponse>> setAmounts(
            @PathVariable Long petId,
            @RequestBody @Valid SlotAmountRequest slotAmountRequest
    ) {
        return ResponseEntity.status(201).body(
                ApiResponse.success(slotService.setSlotAmounts(petId, slotAmountRequest))
        );
    }
}
