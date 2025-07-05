package ject.petfit.global.jwt.refreshtoken.repository;

import ject.petfit.domain.user.entity.AuthUser;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAuthUser(AuthUser authUser);
    Optional<RefreshToken> findByToken(String hashedRefreshToken);
    void deleteByToken(String token);
}
