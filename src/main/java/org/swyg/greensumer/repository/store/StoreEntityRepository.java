package org.swyg.greensumer.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.domain.SellerStoreEntity;
import org.swyg.greensumer.domain.StoreEntity;

import java.util.List;
import java.util.Optional;

public interface StoreEntityRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findAll();
    Page<StoreEntity> findAll(Pageable pageable);
    Page<StoreEntity> findAllBySellerStores(SellerStoreEntity sellerStoreEntity, Pageable pageable);

    Optional<StoreEntity> findByNameAndAddress(String name, AddressEntity address);
    Optional<StoreEntity> findByAddress_Id(Long id);
}

