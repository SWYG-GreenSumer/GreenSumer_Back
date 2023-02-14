package org.swyg.greensumer.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("place_name")
    private String placeName;       // 상호명

    @JsonProperty("address_name")
    private String addressName;     // 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨

    @JsonProperty("y")
    private double latitude;        // 위도(latitude)

    @JsonProperty("x")
    private double longitude;       // 경도(longitude)

    @JsonProperty("distance")
    private double distance;        // 카카오 API에서 제공한 거리
}
