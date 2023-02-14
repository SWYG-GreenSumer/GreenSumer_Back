package org.swyg.greensumer.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ReviewPostLikeEntity;

public interface ReviewPostLikeEntityRepository extends JpaRepository<ReviewPostLikeEntity, Long> {
}
