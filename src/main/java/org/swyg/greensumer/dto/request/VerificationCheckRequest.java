package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCheckRequest {
    private String email;
    private String code;

    public static VerificationCheckRequest of(String email, String code) {
        return new VerificationCheckRequest(email, code);
    }
}
