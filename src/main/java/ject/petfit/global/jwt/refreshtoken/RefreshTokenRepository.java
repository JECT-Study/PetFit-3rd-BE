package ject.petfit.global.jwt.refreshtoken;

import java.util.Optional;
import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAuthUser(AuthUser authUser);
    RefreshToken findByToken(String hashedRefreshToken);
    void deleteByAuthUser(AuthUser user);

    boolean existsByAuthUser(AuthUser user);
}
