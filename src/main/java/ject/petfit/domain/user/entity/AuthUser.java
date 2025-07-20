package ject.petfit.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import ject.petfit.domain.member.entity.Member;
import ject.petfit.global.jwt.refreshtoken.RefreshToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "auth_user")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", updatable = false)
    private Long id;

    private Long kakaoUUID;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name="encoded_password")
    private String encodedPassword;

    @Column(name = "is_new_user", nullable = false)
    private Boolean isNewUser;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "authUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;

    @OneToOne(mappedBy = "authUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @Builder
    public AuthUser(Long kakaoUUID, String email, String name, String nickname, String encodedPassword, boolean isNewUser) {
        this.kakaoUUID = kakaoUUID;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.encodedPassword = encodedPassword;
        this.isNewUser = isNewUser;
    }

    public void addMember(Member member) {
        this.member = member;
        if (member.getAuthUser() != this) {
            member.addAuthUser(this);
        }
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        if (refreshToken.getAuthUser() != this) {
            refreshToken.addAuthUser(this);
        }
    }

    public void changeIsNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public void removeRefreshToken() {
        this.refreshToken = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }

    @Override
    public String getPassword() {
        return this.encodedPassword;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void editNickname(@Size(max = 10, message = "내용은 10자 이내여야 합니다.") String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }
}
