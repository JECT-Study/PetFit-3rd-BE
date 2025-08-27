package ject.petfit.domain.pet.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import ject.petfit.domain.pet.dto.request.PetFavoriteRequestDto;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.request.PetUpdateRequestDto;
import ject.petfit.domain.pet.dto.response.PetFavoriteResponseDto;
import ject.petfit.domain.pet.dto.response.PetListResponseDto;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.service.PetService;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.service.AuthUserService;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final AuthUserService authUserService;

    // Create - 회원가입 직후 (첫 반려동물 등록)
    // Create - 추가
    @PostMapping
    @Operation(summary = "새로운 동물 등록", description = "이름(20자), 종(6타입), 성별(3타입), 생일(YYYY-MM-DD) 형식 제한")
    public ResponseEntity<ApiResponse<PetResponseDto>> createPet(
            @Valid @RequestBody PetRequestDto petDto
    ) {
        Long authUserId = authUserService.loadAuthUserByEmail(petDto.getMemberId()).getId();
        PetResponseDto createdPet = petService.createPet(petDto, authUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(createdPet)
        );
    }

    // Read (Single info of Pet)
    @GetMapping("/{petId}")
    @Operation(summary = "동물 한 마리 정보 조회", description = "반려동물 ID로 반려동물 정보 조회")
    public ResponseEntity<ApiResponse<PetResponseDto>> getPetById(@PathVariable Long petId) {
        PetResponseDto pet = petService.getPetById(petId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(pet)
        );
    }

    // Read (List of Pets)
    @GetMapping("/list/{memberId}")
    @Operation(summary = "모든 동물 정보 조회", description = "한 사용자의 모든 반려동물 정보 조회")
    public ResponseEntity<ApiResponse<List<PetListResponseDto>>> getAllPets(@PathVariable Long memberId) {
        List<PetListResponseDto> pets = petService.getAllPets(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(pets)
        );
    }

    // Update (Pet info)
    @PutMapping("/{petId}")
    @Operation(summary = "동물 정보 수정", description = "반려동물 ID로 반려동물 정보 수정")
    public ResponseEntity<ApiResponse<PetResponseDto>> updatePet(
            @PathVariable Long petId,
            @Valid @RequestBody PetUpdateRequestDto petUpdateRequestDto
    ) {
        PetResponseDto updatedPet = petService.updatePet(petId, petUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(updatedPet)
        );
    }

    // Update (Pet List info) - 즐겨찾기 동물 (isFavorite) 변경
    @PutMapping("/favorites")
    @Operation(summary = "즐겨찾기 동물 업데이트", description = "즐겨찾기 동물을 업데이트 (다른 펫들은 자동으로 false로 설정)")
    public ResponseEntity<ApiResponse<List<PetFavoriteResponseDto>>> updateFavorite(
            @RequestBody PetFavoriteRequestDto requestDto) {
        List<PetFavoriteResponseDto> favoriteResponse = petService.updateFavoriteBatch(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(favoriteResponse)
        );
    }

    // Delete (Pet)
    @DeleteMapping("/{petId}")
    @Operation(summary = "동물 삭제", description = "반려동물 ID로 반려동물 정보 삭제")
    public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.success(null)
        );
    }
}
