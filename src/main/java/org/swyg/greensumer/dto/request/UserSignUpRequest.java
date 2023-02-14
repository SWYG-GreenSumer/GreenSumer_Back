package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {
    private String username;
    private String password;
    private String name;
    private String birth;
    private String nickname;
    private String email;
    private String phone;
    private boolean gender;

    public static UserSignUpRequest of(String username, String password, String email, String nickname) {
        return new UserSignUpRequest(username, password, email, nickname, null, null, null, true);
    }

    public static UserSignUpRequest of(String username, String password, String name, String birth, String nickname, String email, String phone, boolean gender) {
        return new UserSignUpRequest(username, password, name, birth, nickname, email, phone, gender);
    }
}
