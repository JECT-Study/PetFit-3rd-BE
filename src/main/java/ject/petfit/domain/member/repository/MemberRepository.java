package ject.petfit.domain.member.repository;

import java.util.Optional;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAuthUser(AuthUser authUser);
}
