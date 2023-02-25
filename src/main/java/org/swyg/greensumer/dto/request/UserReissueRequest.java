package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserReissueRequest {
    private String accessToken;
    private String refreshToken;

    public static UserReissueRequest of(String accessToken, String refreshToken) {
        return new UserReissueRequest(accessToken, refreshToken);
    }
}
