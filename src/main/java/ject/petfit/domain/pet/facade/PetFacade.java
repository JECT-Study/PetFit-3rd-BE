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
        return petQueryService.getPetById(petId);
    }

    public List<PetListResponseDto> getAllPets(String accessToken) {
        return petQueryService.getAllPets(accessToken);
    }

    // Command Operations
    public PetResponseDto createPet(PetRequestDto petDto, Long authUserId) {
        return petCommandService.createPet(petDto, authUserId);
    }

    public PetResponseDto updatePet(Long petId, PetUpdateRequestDto petUpdateRequestDto) {
        return petCommandService.updatePet(petId, petUpdateRequestDto);
    }

    public void deletePet(Long petId) {
        petCommandService.deletePet(petId);
    }

    public List<PetFavoriteResponseDto> updateFavoriteBatch(PetFavoriteRequestDto dto) {
        return petCommandService.updateFavoriteBatch(dto);
    }
} 