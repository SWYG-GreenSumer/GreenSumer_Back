package org.swyg.greensumer.repository.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductImageEntity;

public interface ProductImageEntityRepository extends JpaRepository<ProductImageEntity, Long> {
}
