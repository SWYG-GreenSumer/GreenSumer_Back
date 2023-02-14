package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private String token;

    public static UserLoginResponse of(String token) {
        return new UserLoginResponse(token);
    }
}
