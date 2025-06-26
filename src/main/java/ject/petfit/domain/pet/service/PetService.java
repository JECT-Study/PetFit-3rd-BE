package ject.petfit.domain.pet.service;


import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetErrorCode;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.exception.AuthUserErrorCode;
import ject.petfit.domain.user.exception.AuthUserException;
import ject.petfit.domain.user.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final AuthUserRepository authUserRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;


    public PetResponseDto createPet(PetRequestDto petDto) {
        AuthUser authUser = authUserRepository.findById(petDto.getAuthUserId())
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.USER_NOT_FOUND));
        Member member = memberRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 최초 등록 (즐겨 찾기) 동물 처리
        Boolean isFirst = authUser.getIsNewUser();

        Pet pet = new Pet(petDto.getName(), petDto.getType(), petDto.getGender(), petDto.getBirthDate(), isFirst);
        pet.setMember(member);

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
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        petRepository.deleteById(id);
    }

}
