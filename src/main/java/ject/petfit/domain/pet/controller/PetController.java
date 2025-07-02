package ject.petfit.domain.pet.controller;


import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import ject.petfit.domain.pet.dto.request.PetFavoriteRequestDTO;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.response.PetFavoriteResponseDTO;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    // Create - 회원가입 직후 (첫 반려동물 등록)
    // Create - 추가
    @PostMapping
    @Operation(summary = "새로운 동물 등록", description = "이름(20자), 종(6타입), 성별(3타입), 생일(YYYY-MM-DD) 형식 제한")
    public ResponseEntity<PetResponseDto> createPet(@RequestBody PetRequestDto petDto) {
        PetResponseDto createdPet = petService.createPet(petDto);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    // Read (Single info of Pet)
    @GetMapping("/{petId}")
    @Operation(summary = "동물 한 마리 정보 조회", description = "반려동물 ID로 반려동물 정보 조회")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable Long petId) {
        PetResponseDto pet = petService.getPetById(petId);
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    // Read (List of Pets)
    @GetMapping
    @Operation(summary = "모든 동물 정보 조회", description = "사용자의 모든 반려동물 정보 조회")
    public ResponseEntity<List<PetResponseDto>> getAllPets() {
        List<PetResponseDto> pets = petService.getAllPets();
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    // Update (Pet info)
    @PutMapping("/{petId}")
    @Operation(summary = "동물 정보 수정", description = "반려동물 ID로 반려동물 정보 수정")
    public ResponseEntity<PetResponseDto> updatePet(@PathVariable Long petId, @RequestBody PetRequestDto petDto) {
        PetResponseDto updatedPet = petService.updatePet(petId, petDto);
        return new ResponseEntity<>(updatedPet, HttpStatus.OK);
    }

    // Update (Pet List info) - 즐겨찾기 동물 (isFavorite) 변경
    @PutMapping("/favorites")
    @Operation(summary = "즐겨찾기 동물 목록 업데이트", description = "즐겨찾기 동물 목록을 일괄 업데이트")
    public ResponseEntity<List<PetFavoriteResponseDTO>> updateFavoritesInBatch(
            @RequestBody List<PetFavoriteRequestDTO> requestDtos) {
        List<PetFavoriteResponseDTO> response = petService.updateFavoriteBatch(requestDtos);
        return ResponseEntity.ok(response);
    }

    // Delete (Pet)
    @DeleteMapping("/{petId}")
    @Operation(summary = "동물 삭제", description = "반려동물 ID로 반려동물 정보 삭제")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
