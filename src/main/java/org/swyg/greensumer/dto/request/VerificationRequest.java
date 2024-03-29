package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    private String email;

    public static VerificationRequest of(String email) {
        return new VerificationRequest(email);
    }
}
