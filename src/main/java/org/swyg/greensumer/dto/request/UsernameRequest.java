package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameRequest {
    private String email;
    private String code;

    public static UsernameRequest of(String email, String code) {
        return new UsernameRequest(email, code);
    }
}
