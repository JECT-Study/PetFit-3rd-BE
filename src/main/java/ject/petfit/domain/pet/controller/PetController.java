package ject.petfit.domain.pet.controller;


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
    public ResponseEntity<PetResponseDto> createPet(@RequestBody PetRequestDto petDto) {
        PetResponseDto createdPet = petService.createPet(petDto);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    // Read (Single info of Pet)
    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable Long id) {
        PetResponseDto pet = petService.getPetById(id);
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    // Read (List of Pets)
    @GetMapping
    public ResponseEntity<List<PetResponseDto>> getAllPets() {
        List<PetResponseDto> pets = petService.getAllPets();
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    // Update (Pet info)
    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDto> updatePet(@PathVariable Long id, @RequestBody PetRequestDto petDto) {
        PetResponseDto updatedPet = petService.updatePet(id, petDto);
        return new ResponseEntity<>(updatedPet, HttpStatus.OK);
    }

    // Update (Pet List info) - 즐겨찾기 동물 (isFavorite) 변경
    @PostMapping("/api/pets/favorites")
    public ResponseEntity<List<PetFavoriteResponseDTO>> updateFavoritesInBatch(
            @RequestBody List<PetFavoriteRequestDTO> requestDtos) {
        List<PetFavoriteResponseDTO> response = petService.updateFavoriteBatch(requestDtos);
        return ResponseEntity.ok(response);
    }

    // Delete (Pet)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
