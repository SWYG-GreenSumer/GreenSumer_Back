package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.swyg.greensumer.api.dto.DocumentDto;
import org.swyg.greensumer.api.dto.KakaoApiResponseDto;
import org.swyg.greensumer.api.service.KakaoAddressSearchService;
import org.swyg.greensumer.domain.RecommendationEntity;
import org.swyg.greensumer.dto.ZeroWasteShopResponse;
import org.swyg.greensumer.dto.request.ZeroWasteShopRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZeroWasteShopRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RecommendationService recommendationService;
    private final Base62Service base62Service;

    @Value("${zerowasteshop.recommendation.base.url}")
    private String baseUrl;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    public List<ZeroWasteShopResponse> recommendZeroWasteShopList(ZeroWasteShopRequest request) {
        // Convert Address to Latitude And Longitude
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(request.getAddress());

        // Validation
        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[ZeroWasteShopRecommendationService - recommendZeroWasteShopList Fail] Input address: {}", request.getAddress());
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);
        List<RecommendationEntity> recommendations = recommendationService.buildRecommendationList(documentDto);
        //List<RecommendationEntity> recommendations = recommendationService.buildRecommendationListByCategoryApi(documentDto);

        return recommendationService.saveAll(recommendations)
                .stream()
                .map(this::convertToRecommendation)
                .collect(Collectors.toList());
    }

    private ZeroWasteShopResponse convertToRecommendation(RecommendationEntity recommendation) {
        return ZeroWasteShopResponse.builder()
                .shopName(recommendation.getTargetShopName())
                .shopAddress(recommendation.getTargetAddress())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(recommendation.getId()))
                .roadViewUrl(ROAD_VIEW_BASE_URL + recommendation.getTargetLatitude() + ", " + recommendation.getTargetLongitude())
                .distance(String.format("%.2f km", recommendation.getDistance()))
                .build();
    }
}
