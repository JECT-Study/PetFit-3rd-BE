package ject.petfit.global.jwt.refreshtoken;

import java.util.Optional;
import ject.petfit.domain.user.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAuthUser(AuthUser authUser);
    RefreshToken findByHashedRefreshToken(String hashedRefreshToken);
}
