package org.swyg.greensumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyg.greensumer.domain.UserEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String nickname;
    private LocalDateTime birth;
    private boolean gender;
    private String roles;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity entity){
        System.out.println(entity.getRoles());
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getFullname(),
                entity.getNickname(),
                entity.getBirth(),
                entity.isGender(),
                entity.getRoles().toString(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRoles()));
    }

    @Override @JsonIgnore public boolean isAccountNonExpired() { return this.deletedAt == null; }
    @Override @JsonIgnore public boolean isAccountNonLocked() { return this.deletedAt == null; }
    @Override @JsonIgnore public boolean isCredentialsNonExpired() { return this.deletedAt == null; }
    @Override @JsonIgnore public boolean isEnabled() { return this.deletedAt == null; }

}
