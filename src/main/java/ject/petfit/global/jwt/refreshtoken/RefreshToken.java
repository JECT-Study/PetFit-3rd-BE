package ject.petfit.global.jwt.refreshtoken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import ject.petfit.domain.user.entity.AuthUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter
@Table(name = "refresh_token")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expires_at;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false, unique = true)
    private AuthUser authUser;

    public RefreshToken(AuthUser authUser, String token, Instant expires_at) {
        this.authUser = authUser;
        this.token = token;
        this.expires_at = expires_at;
    }


    public void addAuthUser(AuthUser authUser) {
        if (this.authUser != null) {
            this.authUser.addRefreshToken(null);
        }
        this.authUser = authUser;
        authUser.addRefreshToken(this);
    }

    public void updateToken(String token, Instant expires_at) {
        this.token = token;
        this.expires_at = expires_at;
    }
}
