package org.swyg.greensumer.repository.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ReviewImageEntity;

import java.util.List;

public interface ReviewImageEntityRepository extends JpaRepository<ReviewImageEntity, Long> {
    List<ReviewImageEntity> findAllByIdIn(List<Long> ids);
}
