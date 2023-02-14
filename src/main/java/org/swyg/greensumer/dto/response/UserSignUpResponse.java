package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.swyg.greensumer.dto.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserSignUpResponse {
    private Long id;
    private String username;
    private String nickname;
    private String fullname;
    private LocalDateTime birth;
    private boolean gender;
    private String email;
    private String roles;

    public static UserSignUpResponse fromUser(User user){
        return new UserSignUpResponse(
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
