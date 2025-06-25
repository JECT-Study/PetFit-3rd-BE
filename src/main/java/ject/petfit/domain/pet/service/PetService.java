package ject.petfit.domain.pet.service;


import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public PetResponseDto createPet(PetRequestDto petDto) {
        Pet pet = new Pet(petDto.getName(), petDto.getType(), petDto.getGender(), petDto.getBirthDate(), petDto.getIsFirst());
        Pet savedPet = petRepository.save(pet);
        return new PetResponseDto(savedPet.getId(), savedPet.getName(), savedPet.getType(), savedPet.getGender(),
                savedPet.getBirthDate(), savedPet.getIsFirst());
    }

    public PetResponseDto getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        return new PetResponseDto(pet.getId(), pet.getName(), pet.getType(), pet.getGender(),
                pet.getBirthDate(), pet.getIsFirst());
    }

    public List<PetResponseDto> getAllPets() {
        return petRepository.findAll()
                .stream()
                .map(p -> new PetResponseDto(p.getId(), p.getName(), p.getType(), p.getGender(),
                        p.getBirthDate(), p.getIsFirst()))
                .collect(Collectors.toList());
    }

    public PetResponseDto updatePet(Long id, PetRequestDto petDto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        pet.updatePet(petDto);
        Pet updatedPet = petRepository.save(pet);
        return new PetResponseDto(updatedPet.getId(), updatedPet.getName(), updatedPet.getType(),
                updatedPet.getGender(), updatedPet.getBirthDate(), updatedPet.getIsFirst());
    }


    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

}
