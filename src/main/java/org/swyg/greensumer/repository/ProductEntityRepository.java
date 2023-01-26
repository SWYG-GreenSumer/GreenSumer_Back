package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductEntity;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {

}
