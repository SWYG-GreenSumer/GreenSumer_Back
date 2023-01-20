package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerificationCheckRequest {
    private String email;
    private String code;

    public static VerificationCheckRequest of(String email, String code) {
        return new VerificationCheckRequest(email, code);
    }
}
