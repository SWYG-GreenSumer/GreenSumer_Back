package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.RecommendationEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double RADIUS_KM = 30.0;   // 반경 30 km

    private final StoreService storeService;

    public List<RecommendationEntity> buildRecommendationList(double lat, double lng) {
        return storeService.searchStoreList().stream().map(store ->
                        RecommendationEntity.builder()
                                .curLatitude(lat)
                                .curLongitude(lng)
                                .targetShopName(store.getName())
                                .targetAddress(store.getAddress().getAddress())
                                .targetLatitude(store.getAddress().getLat())
                                .targetLongitude(store.getAddress().getLng())
                                .distance(calculateDistance(lat, lng, store.getAddress().getLat(), store.getAddress().getLng()))
                                .targetId(store.getId())
                                .targetDescription(store.getDescription())
                                .targetHours(store.getHours())
                                .targetPhone(store.getPhone())
                                .targetSns(store.getUrl())
                                .build())
                .filter(recommendation -> recommendation.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(RecommendationEntity::getDistance))
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
}
