package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.swyg.greensumer.domain.constant.RoleType;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false, length = 50) private String username;
    @Setter @Column(nullable = false) private String password;
    @Setter @Column(nullable = false, length = 100) private String email;
    @Setter @Column(nullable = false, length = 50) private String nickname;

    @Setter @Column(nullable = false) private RoleType grade;

    protected UserAccount(){}

    private UserAccount(String username, String password, String email, String nickname, RoleType grade) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.grade = grade;
    }

    public static UserAccount of(String username, String password, String email, String nickname, RoleType grade){
        return new UserAccount(username, password, email, nickname, grade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}