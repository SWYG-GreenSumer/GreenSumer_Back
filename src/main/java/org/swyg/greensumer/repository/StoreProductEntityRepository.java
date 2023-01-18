package org.swyg.greensumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.StoreProductEntity;

import java.util.Optional;

public interface StoreProductEntityRepository extends JpaRepository<StoreProductEntity, Integer> {

    Page<StoreProductEntity> findAllByStore(StoreEntity storeEntity, Pageable pageable);

    Optional<StoreProductEntity> findByStore_IdAndProduct_Id(Integer storeId, Integer productId);

    void deleteByProduct_Id(Integer productId);
}
