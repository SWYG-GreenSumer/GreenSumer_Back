package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.UserRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(name = "user", indexes = {
        @Index(name = "username_idx", columnList = "username", unique = true),
        @Index(name = "email_idx", columnList = "email", unique = true),
        @Index(name = "nickname_idx", columnList = "nickname", unique = true)
})
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class UserEntity {

    @Setter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter @Column(name = "username", length = 50) private String username;
    @Setter @Column(name = "password") private String password;
    @Setter @Column(nullable = false, length = 100) private String email;
    @Setter @Column(nullable = false, length = 50) private String nickname;

    @Setter @OneToOne @JoinColumn(name = "address_id") AddressEntity addressEntity;

    @Setter @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }

    @PreUpdate
    void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}

    public static UserEntity of(String username, String password, String nickname, String email){
        return UserEntity.of(username, password, nickname, email, null);
    }

    public static UserEntity of(String username, String password, String nickname, String email, AddressEntity address){
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setAddressEntity(address);
        user.setRole(address == null ? UserRole.USER : UserRole.SELLER);
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
