package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullname;
    private LocalDateTime birth;
    private boolean gender;
    private String nickname;
    private String roles;

    public static UpdateUserResponse fromUser(User user) {
        return new UpdateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getFullname(),
                user.getBirth(),
                user.isGender(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
