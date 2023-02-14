package org.swyg.greensumer.domain;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@ToString(callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", indexes = {
        @Index(name = "username_idx", columnList = "username", unique = true),
        @Index(name = "email_idx", columnList = "email", unique = true),
        @Index(name = "nickname_idx", columnList = "nickname", unique = true)
})
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class UserEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50) private String username;
    @Column(nullable = false, length = 50) private String nickname;
    @Column(nullable = false, length = 50) private String fullname;

    @Column(nullable = false, length = 100) private String email;

    @Column(nullable = false) private String password;

    @Column(nullable = false) private LocalDateTime birth;
    @Column(nullable = false) private boolean gender;

    @Column(name = "role") @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

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

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUserInfo(String password, String nickname, String email) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public void updateRole(String role) {
        this.roles.add(UserRole.valueOf(role).toString());
    }
}
