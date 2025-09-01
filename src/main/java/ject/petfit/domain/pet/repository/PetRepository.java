package ject.petfit.domain.pet.repository;

import java.util.List;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.member WHERE p.member.id = :memberId")
    List<Pet> findByMemberIdWithMember(@Param("memberId") Long memberId);

    // 특정 멤버의 모든 펫을 한 번에 조회 (즐겨찾기 업데이트용)
    @Query("SELECT p FROM Pet p WHERE p.member.id = :memberId")
    List<Pet> findByMemberId(@Param("memberId") Long memberId);
    
    // 배치 업데이트로 성능 향상
    @Modifying
    @Query("UPDATE Pet p SET p.isFavorite = :isFavorite WHERE p.member.id = :memberId")
    void updateAllPetsFavoriteByMemberId(@Param("memberId") Long memberId, @Param("isFavorite") Boolean isFavorite);
    
    // 특정 펫만 즐겨찾기 업데이트
    @Modifying
    @Query("UPDATE Pet p SET p.isFavorite = :isFavorite WHERE p.id = :petId")
    void updatePetFavoriteById(@Param("petId") Long petId, @Param("isFavorite") Boolean isFavorite);

}
