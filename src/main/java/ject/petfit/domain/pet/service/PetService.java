package ject.petfit.domain.pet.service;


import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.exception.MemberErrorCode;
import ject.petfit.domain.member.exception.MemberException;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.pet.dto.request.PetFavoriteRequestDto;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.request.PetUpdateRequestDto;
import ject.petfit.domain.pet.dto.response.PetFavoriteResponseDto;
import ject.petfit.domain.pet.dto.response.PetListResponseDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Facade Pattern + CQRS Pattern 도입으로 사용하지는 않으나 유지
@Service
@RequiredArgsConstructor
public class PetService {
    private final AuthUserRepository authUserRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public PetResponseDto createPet(PetRequestDto petDto, Long authUserId) {
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new AuthUserException(AuthUserErrorCode.USER_NOT_FOUND));
        Member member = memberRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 최초 등록 (즐겨 찾기) 동물 처리
        Boolean isFavorite = authUser.getIsNewUser();

        Pet pet = new Pet(petDto.getName(), petDto.getType(), petDto.getGender(), petDto.getBirthDate(), isFavorite);
        pet.setMember(member);

        Pet savedPet = petRepository.save(pet);
        return new PetResponseDto(savedPet.getId(), savedPet.getName(), savedPet.getType(), savedPet.getGender(),
                savedPet.getBirthDate(), savedPet.getIsFavorite());
    }

    public PetResponseDto getPetById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        return new PetResponseDto(pet.getId(), pet.getName(), pet.getType(), pet.getGender(),
                pet.getBirthDate(), pet.getIsFavorite());
    }

    public List<PetListResponseDto> getAllPets(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return petRepository.findByMemberId(member.getId())
                .stream()
                .map(p -> new PetListResponseDto(p.getId(), p.getName(), p.getType(), p.getIsFavorite()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PetResponseDto updatePet(Long petId, PetUpdateRequestDto petUpdateRequestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        Member member = memberRepository.findById(petUpdateRequestDto.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!pet.getMember().equals(member)) {
            throw new PetException(PetErrorCode.PET_NOT_BELONG_TO_MEMBER);
        }

        pet.updatePet(
                petUpdateRequestDto.getName(),
                petUpdateRequestDto.getType(),
                petUpdateRequestDto.getGender(),
                petUpdateRequestDto.getBirthDate()
        );

        Pet updatedPet = petRepository.save(pet);
        return new PetResponseDto(updatedPet.getId(), updatedPet.getName(), updatedPet.getType(),
                updatedPet.getGender(), updatedPet.getBirthDate(), updatedPet.getIsFavorite());
    }


    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        petRepository.deleteById(petId);
    }

    @Transactional
    public List<PetFavoriteResponseDto> updateFavoriteBatch(PetFavoriteRequestDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        List<PetFavoriteResponseDto> petFavoriteResponseDtos = new ArrayList<>();

        if (Boolean.TRUE.equals(dto.getIsFavorite())) {
            // 해당 멤버의 모든 펫을 false로 설정
            List<Pet> memberPets = petRepository.findByMemberId(pet.getMember().getId());
            for (Pet memberPet : memberPets) {
                memberPet.updateIsFavorite(false);
                petFavoriteResponseDtos.add(new PetFavoriteResponseDto(memberPet.getId(), memberPet.getIsFavorite()));
            }
            
            // 요청된 펫만 true로 설정
            pet.updateIsFavorite(true);
            petFavoriteResponseDtos.add(new PetFavoriteResponseDto(pet.getId(), pet.getIsFavorite()));
            
            petRepository.saveAll(memberPets);
        } else {
            pet.updateIsFavorite(false);
            petRepository.save(pet);
        }
        
        return petFavoriteResponseDtos;
    }
}

