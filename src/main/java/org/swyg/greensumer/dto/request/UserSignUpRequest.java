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
    private String nickname;
    private String email;
    private Integer storeId;
    private String address;
    private Double lat;
    private Double lng;

    public static UserSignUpRequest of(String username, String password, String email, String nickname) {
        return new UserSignUpRequest(username, password, email, nickname, null, null, null, null);
    }

    public static UserSignUpRequest of(String username, String password, String email, String nickname, Integer storeId, String address, Double lat, Double lng) {
        return new UserSignUpRequest(username, password, email, nickname, storeId, address, lat, lng);
    }

}
