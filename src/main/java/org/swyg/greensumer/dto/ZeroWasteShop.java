package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZeroWasteShop {
    private Long id;
    private String shopName;
    private String shopAddress;
    private double latitude;
    private double longitude;
}
