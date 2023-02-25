package org.swyg.greensumer.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
public class UserEntity extends DateTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50) private String username;
    @Column(nullable = false, length = 50) private String nickname;
    @Column(nullable = false, length = 50) private String fullname;
    @Column(nullable = false, length = 50) private String phone;

    @Column(nullable = false, length = 100) private String email;

    @Column(nullable = false) private String password;

    @JsonSerialize(using = LocalDateSerializer.class) @JsonDeserialize(using = LocalDateDeserializer.class)
    @Column(nullable = false) private LocalDate birth;
    @Column(nullable = false) private boolean gender;

    @Column(name = "role") @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<UserRole> roles = new LinkedHashSet<>();

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

    public void updateRole(UserRole role) {
        this.roles.add(role);
    }
}
