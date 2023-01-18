package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String address;
    private Double lat;
    private Double lng;

    public static UpdateUserRequest of(String username, String password, String email, String nickname, String address, Double lat, Double lng) {
        return new UpdateUserRequest(username, password, email, nickname, address, lat, lng);
    }

}
