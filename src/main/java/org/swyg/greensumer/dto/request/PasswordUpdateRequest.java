package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

    private String username;
    private String email;
    private String code;
    private String password;

    public static PasswordUpdateRequest of(String username, String email, String code, String password) {
        return new PasswordUpdateRequest(username, email, code, password);
    }
}
