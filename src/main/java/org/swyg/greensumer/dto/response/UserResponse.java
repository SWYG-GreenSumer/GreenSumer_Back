package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String fullname;
    private String phone;
    private LocalDate birth;
    private boolean gender;
    private String email;
    private String roles;

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getFullname(),
                user.getPhone(),
                user.getBirth(),
                user.isGender(),
                user.getEmail(),
                user.getRoles()
        );
    }

}
