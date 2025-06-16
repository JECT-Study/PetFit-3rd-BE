package ject.petfit.domain.user.repository;

import java.util.Optional;
import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByEmail(String email);

}
