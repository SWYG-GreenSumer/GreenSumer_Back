package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class ShopRecommendationService {

    private final RecommendationService recommendationService;

    public List<ZeroWasteShopResponse> recommendZeroWasteShopList(ZeroWasteShopRequest request) {

        if (Objects.isNull(request.getLat()) || Objects.isNull(request.getLng())) {
            log.error("[ZeroWasteShopRecommendationService - recommendZeroWasteShopList Fail] Current coordinates : {}, {}", request.getLat(), request.getLng());
            return Collections.emptyList();
        }

        List<RecommendationEntity> recommendations = recommendationService.buildRecommendationList(convertToDouble(request.getLat()), convertToDouble(request.getLng()));

        return recommendations.stream()
                .map(this::convertToRecommendation)
                .collect(Collectors.toList());
    }

    private double convertToDouble(String value) {
        return Double.parseDouble(value);
    }

    private ZeroWasteShopResponse convertToRecommendation(RecommendationEntity recommendation) {
        return ZeroWasteShopResponse.builder()
                .shopId(recommendation.getTargetId())
                .shopName(recommendation.getTargetShopName())
                .shopAddress(recommendation.getTargetAddress())
                .shopHours(recommendation.getTargetHours())
                .shopPhone(recommendation.getTargetPhone())
                .shopSns(recommendation.getTargetSns())
                .shopDescription(recommendation.getTargetDescription())
                .distance(String.format("%.2f km", recommendation.getDistance()))
                .build();
    }
}
