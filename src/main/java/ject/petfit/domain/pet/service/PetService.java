package ject.petfit.domain.pet.service;


import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet updatePet(Long id, Pet pet) {
        Pet existingPet = getPetById(id);
        existingPet.updatePet(pet);
        return petRepository.save(existingPet);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

}
