package org.swyg.greensumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.SellerStoreEntity;

import java.util.Optional;

public interface SellerStoreEntityRepository extends JpaRepository<SellerStoreEntity, Integer> {

    Optional<SellerStoreEntity> findBySeller_IdAndStore_Id(Integer seller_Id, Integer store_id);

    void deleteAllByStore_Id(Integer store_id);

    Page<SellerStoreEntity> findAllBySeller_Id(Integer seller_Id, Pageable pageable);
}
