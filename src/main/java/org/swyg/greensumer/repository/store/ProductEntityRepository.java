package org.swyg.greensumer.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductEntity;

import java.util.List;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByIdIn(List<Long> id);
}
