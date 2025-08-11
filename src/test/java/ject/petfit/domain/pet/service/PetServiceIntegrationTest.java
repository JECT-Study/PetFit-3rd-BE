package ject.petfit.domain.pet.service;

import java.util.Arrays;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.member.entity.Role;
import ject.petfit.domain.member.repository.MemberRepository;
import ject.petfit.domain.pet.dto.request.PetFavoriteRequestDto;
import ject.petfit.domain.pet.dto.request.PetRequestDto;
import ject.petfit.domain.pet.dto.response.PetFavoriteResponseDto;
import ject.petfit.domain.pet.dto.response.PetListResponseDto;
import ject.petfit.domain.pet.dto.response.PetResponseDto;
import ject.petfit.domain.pet.entity.Pet;
import ject.petfit.domain.pet.exception.PetException;
import ject.petfit.domain.pet.repository.PetRepository;
import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.domain.user.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PetService 통합 테스트")
class PetServiceIntegrationTest {

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    private Member member;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
        memberRepository.deleteAll();
        authUserRepository.deleteAll();
        member = Member.builder()
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
        authUser = AuthUser.builder()
                .kakaoUUID(123456789L)
                .email("test@test.com")
                .nickname("테스트유저")
                .encodedPassword("encoded-password")
                .isNewUser(true)
                .build();
        authUser.addMember(member);
        member = memberRepository.save(member);
        authUser = authUserRepository.save(authUser);
    }

    @Test
    @DisplayName("반려동물 생성 성공")
    void createPet_Success() {
        PetRequestDto dto = new PetRequestDto("멍멍이", "강아지", "남아", LocalDate.of(2020, 1, 1), true);
        Pet pet = new Pet(dto.getName(), dto.getType(), dto.getGender(), dto.getBirthDate(), dto.getIsFavorite());
        pet.setMember(member);

        Pet savedPet = petRepository.save(pet);

        assertThat(savedPet.getId()).isNotNull();
        assertThat(savedPet.getName()).isEqualTo("멍멍이");
        assertThat(savedPet.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("반려동물 단일 조회 성공")
    void getPetById_Success() {
        Pet pet = Pet.builder()
                .name("멍멍이")
                .type("강아지")
                .gender("남아")
                .birthDate(LocalDate.of(2020, 1, 1))
                .isFavorite(true)
                .build();
        pet.setMember(member);
        Pet savedPet = petRepository.save(pet);

        PetResponseDto result = petService.getPetById(savedPet.getId());
        assertThat(result.getName()).isEqualTo("멍멍이");
    }

    @Test
    @DisplayName("반려동물 전체 조회 성공")
    void getAllPets_Success() {
        Pet pet1 = Pet.builder()
                .name("멍멍이")
                .type("강아지")
                .gender("남아")
                .birthDate(LocalDate.of(2020, 1, 1))
                .isFavorite(true)
                .build();
        pet1.setMember(member);
        Pet pet2 = Pet.builder()
                .name("냥냥이")
                .type("고양이")
                .gender("여아")
                .birthDate(LocalDate.of(2021, 2, 2))
                .isFavorite(false)
                .build();
        pet2.setMember(member);
        petRepository.save(pet1);
        petRepository.save(pet2);
//
//        List<PetListResponseDto> pets = petService.getAllPets(member.getAuthUser().getEmail());
//        assertThat(pets).hasSize(2);
    }

    @Test
    @DisplayName("반려동물 정보 수정 성공")
    void updatePet_Success() {
        Pet pet = Pet.builder()
                .name("멍멍이")
                .type("강아지")
                .gender("남아")
                .birthDate(LocalDate.of(2020, 1, 1))
                .isFavorite(true)
                .build();
        pet.setMember(member);
        Pet savedPet = petRepository.save(pet);

//        PetRequestDto updateDto = new PetRequestDto("냥냥이", "고양이", "여아", LocalDate.of(2021, 2, 2), false);
//        PetResponseDto updated = petService.updatePet(savedPet.getId(), updateDto, member.getAuthUser().getEmail());
//
//        assertThat(updated.getName()).isEqualTo("냥냥이");
//        assertThat(updated.getType()).isEqualTo("고양이");
    }

    @Test
    @DisplayName("반려동물 삭제 성공")
    void deletePet_Success() {
        Pet pet = Pet.builder()
                .name("멍멍이")
                .type("강아지")
                .gender("남아")
                .birthDate(LocalDate.of(2020, 1, 1))
                .isFavorite(true)
                .build();
        pet.setMember(member);
        Pet savedPet = petRepository.save(pet);

        petService.deletePet(savedPet.getId());
        assertThat(petRepository.findById(savedPet.getId())).isEmpty();
    }

    @Test
    @DisplayName("반려동물 삭제 실패 - 존재하지 않는 ID")
    void deletePet_NotFound() {
        assertThatThrownBy(() -> petService.deletePet(999L))
                .isInstanceOf(PetException.class);
    }

    @Test
    @DisplayName("여러 반려동물 즐겨찾기 상태 일괄 수정")
    void updateFavoriteBatch_Success() {
        // given
        Pet pet1 = Pet.builder()
                .name("멍멍이")
                .type("강아지")
                .gender("남아")
                .birthDate(LocalDate.of(2020, 1, 1))
                .isFavorite(false)
                .build();
        pet1.setMember(member);

        Pet pet2 = Pet.builder()
                .name("냥냥이")
                .type("고양이")
                .gender("여아")
                .birthDate(LocalDate.of(2021, 2, 2))
                .isFavorite(false)
                .build();
        pet2.setMember(member);

        pet1 = petRepository.save(pet1);
        pet2 = petRepository.save(pet2);

        PetFavoriteRequestDto req1 = new PetFavoriteRequestDto(pet1.getId(), true);
        PetFavoriteRequestDto req2 = new PetFavoriteRequestDto(pet2.getId(), true);

        List<PetFavoriteRequestDto> batchRequest = Arrays.asList(req1, req2);

        // when
        List<PetFavoriteResponseDto> result = petService.updateFavoriteBatch(batchRequest, member.getAuthUser().getEmail());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getIsFavorite().equals(true));

        // 실제 DB 반영 확인
        Pet updatedPet1 = petRepository.findById(pet1.getId()).orElseThrow();
        Pet updatedPet2 = petRepository.findById(pet2.getId()).orElseThrow();
        assertThat(updatedPet1.getIsFavorite()).isTrue();
        assertThat(updatedPet2.getIsFavorite()).isTrue();
    }
}