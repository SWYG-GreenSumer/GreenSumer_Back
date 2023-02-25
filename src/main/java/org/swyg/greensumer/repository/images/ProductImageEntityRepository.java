package org.swyg.greensumer.repository.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductImageEntity;

import java.util.List;

public interface ProductImageEntityRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findAllByIdIn(List<Long> ids);

}
