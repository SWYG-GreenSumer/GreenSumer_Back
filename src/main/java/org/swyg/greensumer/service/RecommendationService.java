package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.swyg.greensumer.api.dto.DocumentDto;
import org.swyg.greensumer.api.service.KakaoCategorySearchService;
import org.swyg.greensumer.domain.RecommendationEntity;
import org.swyg.greensumer.repository.RecommendationEntityRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int MAX_SEARCH_COUNT = 5;  // 최대 검색 갯수
    private static final double RADIUS_KM = 10.0;   // 반경 10 km
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    private final ZeroWasterShopEntitySearchService zeroWasterShopEntitySearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final RecommendationEntityRepository recommendationEntityRepository;
    private final Base62Service base62Service;

    public List<RecommendationEntity> saveAll(List<RecommendationEntity> directions) {
        if(CollectionUtils.isEmpty(directions)) return Collections.emptyList();
        return recommendationEntityRepository.saveAll(directions);
    }

    public String findRecommendationUrlById(String encodedId) {
        Long decodeId = base62Service.decodeDirectionId(encodedId);
        RecommendationEntity recommendation = recommendationEntityRepository.findById(decodeId).orElse(null);

        String params = String.join(",", recommendation.getTargetShopName(), String.valueOf(recommendation.getTargetLatitude()), String.valueOf(recommendation.getTargetLongitude()));

        return UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();
    }

    public List<RecommendationEntity> buildRecommendationList(DocumentDto documentDto) {
        if(Objects.isNull(documentDto)) return Collections.emptyList();

        return zeroWasterShopEntitySearchService.searchShopDtoList()
                .stream().map(shopDto ->
                        RecommendationEntity.builder()
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                .targetShopName(shopDto.getShopName())
                                .targetAddress(shopDto.getShopAddress())
                                .targetLatitude(shopDto.getLatitude())
                                .targetLongitude(shopDto.getLongitude())
                                .distance(
                                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                                shopDto.getLatitude(), shopDto.getLongitude())
                                )
                                .build())
                .filter(recommendation -> recommendation.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(RecommendationEntity::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    public List<RecommendationEntity> buildRecommendationList(double lat, double lng) {
        return zeroWasterShopEntitySearchService.searchShopDtoList()
                .stream().map(shop ->
                        RecommendationEntity.builder()
                                .inputLatitude(lat)
                                .inputLongitude(lng)
                                .targetShopName(shop.getShopName())
                                .targetAddress(shop.getShopAddress())
                                .targetLatitude(shop.getLatitude())
                                .targetLongitude(shop.getLongitude())
                                .distance(calculateDistance(lat, lng, shop.getLatitude(), shop.getLongitude()))
                                .build())
                .filter(recommendation -> recommendation.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(RecommendationEntity::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }

    public List<RecommendationEntity> buildRecommendationListByCategoryApi(DocumentDto inputDocumentDto) {
        if(Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentList()
                .stream().map(resultDocumentDto ->
                        RecommendationEntity.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetShopName(resultDocumentDto.getPlaceName())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                .distance(resultDocumentDto.getDistance() * 0.001) // km 단위
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }
}
