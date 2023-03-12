package org.swyg.greensumer.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.StoreProductEntity;

import java.util.List;
import java.util.Optional;

public interface StoreProductEntityRepository extends JpaRepository<StoreProductEntity, Long> {

    Page<StoreProductEntity> findAllByStore(StoreEntity storeEntity, Pageable pageable);

    Optional<StoreProductEntity> findByStore_IdAndProduct_Id(Long storeId, Long productId);

    List<StoreProductEntity> findAllByStore_IdAndProductIn(Long storeId, List<Long> productsId);
    List<StoreProductEntity> findAllByStore_IdAndProduct_IdIn(Long storeId, List<Long> productsId);
}
