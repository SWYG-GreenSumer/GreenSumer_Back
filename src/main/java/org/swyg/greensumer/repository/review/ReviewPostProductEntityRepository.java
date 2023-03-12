package org.swyg.greensumer.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ReviewPostProductEntity;

import java.util.List;

public interface ReviewPostProductEntityRepository extends JpaRepository<ReviewPostProductEntity, Long> {

    List<ReviewPostProductEntity> findAllByReview_Id(Long id);
}
