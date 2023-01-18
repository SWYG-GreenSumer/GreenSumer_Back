package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ProductEntity;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Integer> {

//    void deleteAllByStore(StoreEntity storeEntity);
//
//    Page<ProductEntity> findAllByStore(StoreEntity storeEntity, Pageable pageable);
//
//    Optional<ProductEntity> findByStoreAndId(StoreEntity storeEntity, Integer id);

}
