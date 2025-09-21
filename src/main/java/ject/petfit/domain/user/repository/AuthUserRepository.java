package ject.petfit.domain.user.repository;

import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByMemberId(Long memberId);

    @Query("SELECT au FROM AuthUser au LEFT JOIN FETCH au.member m LEFT JOIN FETCH m.pets WHERE au.member.id = :memberId")
Optional<AuthUser> findByMemberIdWithPets(@Param("memberId") Long memberId);
}
