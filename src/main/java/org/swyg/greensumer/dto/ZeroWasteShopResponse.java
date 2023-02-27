package org.swyg.greensumer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ZeroWasteShopResponse {

    private Long shopId;
    private String shopName;        // 가게 명
    private String shopAddress;     // 가게 주소
//    private String directionUrl;    // 길안내 url
//    private String roadViewUrl;     // 로드뷰 url
    private String distance;        // 고객 주소와 약국 주소의 거리

    private String shopDescription;
    private String shopHours;
    private String shopPhone;
    private String shopSns;

    // TODO : 이미지 정보를 넣어주어야 한다.
}
