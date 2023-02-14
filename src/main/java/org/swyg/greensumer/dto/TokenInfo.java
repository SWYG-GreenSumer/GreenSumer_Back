package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenInfo {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long refreshTokenExpirationTime;
}
