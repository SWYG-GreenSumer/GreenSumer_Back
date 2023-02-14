package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.RecommendationEntity;

public interface RecommendationEntityRepository extends JpaRepository<RecommendationEntity, Long> {
}
