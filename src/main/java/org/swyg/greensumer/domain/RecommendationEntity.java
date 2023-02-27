package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "recommendation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RecommendationEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 고객
    private double curLatitude;
    private double curLongitude;

    // 제로웨이스트샵
    private String targetShopName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    private Long targetId;
    private String targetDescription;
    private String targetHours;
    private String targetPhone;
    private String targetSns;
//    private Set<StoreImageEntity> storeImageEntities;

    // 고객 주소 와 제로웨이스트샵 사이의 거리
    private double distance;
}
