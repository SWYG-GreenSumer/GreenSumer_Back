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
    private String email;
    private String nickname;
    private String address;
    private String lat;
    private String lng;

    public static UserSignUpRequest of(String username, String password, String email, String nickname) {
        return new UserSignUpRequest(username, password, email, nickname, null, null, null);
    }

    public static UserSignUpRequest of(String username, String password, String email, String nickname, String address, String lat, String lng) {
        return new UserSignUpRequest(username, password, email, nickname, address, lat, lng);
    }

}
