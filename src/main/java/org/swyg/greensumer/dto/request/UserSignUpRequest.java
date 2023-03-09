package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {
    private String username;
    private String password;
    private String name;
    private String birth;
    private String nickname;
    private String email;
    private String phone;
    private boolean gender;

    public static UserSignUpRequest of(String username, String password, String name, String birth, String nickname, String email, String phone, boolean gender) {
        return UserSignUpRequest.builder()
                .username(username)
                .password(password)
                .name(name)
                .birth(birth)
                .nickname(nickname)
                .email(email)
                .phone(phone)
                .gender(gender)
                .build();
    }
}
