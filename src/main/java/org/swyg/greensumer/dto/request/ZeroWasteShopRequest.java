package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ZeroWasteShopRequest {
    private String address;

    public static ZeroWasteShopRequest of(String address) {
        return new ZeroWasteShopRequest(address);
    }
}
