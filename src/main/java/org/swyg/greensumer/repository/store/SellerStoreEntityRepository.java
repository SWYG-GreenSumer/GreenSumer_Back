package org.swyg.greensumer.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.SellerStoreEntity;

import java.util.Optional;

public interface SellerStoreEntityRepository extends JpaRepository<SellerStoreEntity, Long> {

    Optional<SellerStoreEntity> findBySeller_IdAndStore_Id(Long seller_Id, Long store_id);

    void deleteAllByStore_Id(Long store_id);

    Page<SellerStoreEntity> findAllBySeller_Id(Long seller_Id, Pageable pageable);
}
