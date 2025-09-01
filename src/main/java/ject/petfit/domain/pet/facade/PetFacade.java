package ject.petfit.domain.pet.facade;

import ject.petfit.domain.pet.dto.request.PetFavoriteRequestDto;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.request.PetUpdateRequestDto;
import ject.petfit.domain.pet.dto.response.PetFavoriteResponseDto;
import ject.petfit.domain.pet.dto.response.PetListResponseDto;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.service.PetCommandService;
import ject.petfit.domain.pet.service.PetQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetFacade {

    private final PetCommandService petCommandService;
    private final PetQueryService petQueryService;

    // Query Operations
    public PetResponseDto getPetById(Long petId) {
        log.info("Facade: Getting pet by ID: {}", petId);
        return petQueryService.getPetById(petId);
    }

    public List<PetListResponseDto> getAllPets(Long memberId) {
        log.info("Facade: Getting all pets for member ID: {}", memberId);
        return petQueryService.getAllPets(memberId);
    }

    // Command Operations
    public PetResponseDto createPet(PetRequestDto petDto, Long authUserId) {
        log.info("Facade: Creating pet for authUserId: {}", authUserId);
        return petCommandService.createPet(petDto, authUserId);
    }

    public PetResponseDto updatePet(Long petId, PetUpdateRequestDto petUpdateRequestDto) {
        log.info("Facade: Updating pet with ID: {}", petId);
        return petCommandService.updatePet(petId, petUpdateRequestDto);
    }

    public void deletePet(Long petId) {
        log.info("Facade: Deleting pet with ID: {}", petId);
        petCommandService.deletePet(petId);
    }

    public List<PetFavoriteResponseDto> updateFavoriteBatch(PetFavoriteRequestDto dto) {
        log.info("Facade: Updating favorite for pet with ID: {}", dto.getPetId());
        return petCommandService.updateFavoriteBatch(dto);
    }
} 