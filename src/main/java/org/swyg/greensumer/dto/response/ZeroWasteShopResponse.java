package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ZeroWasteShopResponse {
    private String shopName;        // 가게 명
    private String shopAddress;     // 가게 주소
    private String directionUrl;    // 길안내 url
    private String roadViewUrl;     // 로드뷰 url
    private String distance;        // 고객 주소와 약국 주소의 거리
}
