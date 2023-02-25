package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ZeroWasteShopRequest {
    private String lat;
    private String lng;

    public static ZeroWasteShopRequest of(String lat, String lng) {
        return new ZeroWasteShopRequest(lat, lng);
    }
}
