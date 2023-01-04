package org.swyg.greensumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.StoreEntity;

import java.util.Optional;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Integer> {

    void deleteAllByStore(StoreEntity storeEntity);

    Page<ProductEntity> findAllByStore(StoreEntity storeEntity, Pageable pageable);

    Optional<ProductEntity> findByStoreAndId(StoreEntity storeEntity, Integer id);
}
