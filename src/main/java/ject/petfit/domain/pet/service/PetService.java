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

    public List<PetListResponseDto> getAllPets(String memberEmail) {
        Member member = memberRepository.findByAuthUserEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return petRepository.findByMember(member)
                .stream()
                .map(p -> new PetListResponseDto(p.getId(), p.getName(), p.getIsFavorite()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PetResponseDto updatePet(Long petId, PetRequestDto petDto, String memberEmail) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        Member member = memberRepository.findByAuthUserEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!pet.getMember().equals(member)) {
            throw new PetException(PetErrorCode.PET_NOT_BELONG_TO_MEMBER);
        }

        pet.updatePet(
                petDto.getName(),
                petDto.getType(),
                petDto.getGender(),
                petDto.getBirthDate(),
                petDto.getIsFavorite()
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
    public List<PetFavoriteResponseDto> updateFavoriteBatch(List<PetFavoriteRequestDto> dtos, String memberEmail) {
        // ID 추출 (WHERE IN 절 사용)
        List<Long> petIds = dtos.stream()
                .map(dto -> dto.getPetId())
                .collect(Collectors.toList());

        // 한 번의 쿼리로 엔티티 조회
        Map<Long, Pet> petMap = petRepository.findByIdIn(petIds)
                .stream()
                .collect(Collectors.toMap(Pet::getId, pet -> pet));

        Member member = memberRepository.findByAuthUserEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 업데이트 작업
        List<Pet> updatedPets = new ArrayList<>();
        for (PetFavoriteRequestDto dto : dtos) {
            Pet pet = petMap.get(dto.getPetId());
            if (!pet.getMember().equals(member)) {
                throw new PetException(PetErrorCode.PET_NOT_BELONG_TO_MEMBER);
            }
            pet.updateIsFavorite(dto.getIsFavorite());
            updatedPets.add(pet);
        }
        // 일괄 저장 (batch update)
        petRepository.saveAll(updatedPets);
        // 응답 생성
        return updatedPets.stream()
                .map(pet -> new PetFavoriteResponseDto(pet.getId(), pet.getIsFavorite()))
                .collect(Collectors.toList());
    }
}
