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

    public static UpdateUserRequest of(String username, String password, String email, String nickname) {
        return new UpdateUserRequest(username, password, email, nickname);
    }

}
