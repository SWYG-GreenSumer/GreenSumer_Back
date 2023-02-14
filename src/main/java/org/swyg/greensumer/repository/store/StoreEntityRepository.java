package org.swyg.greensumer.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.domain.SellerStoreEntity;
import org.swyg.greensumer.domain.StoreEntity;

import java.util.Optional;

public interface StoreEntityRepository extends JpaRepository<StoreEntity, Long> {
    boolean existsByName(String name);

    Page<StoreEntity> findAllBySellerStores(SellerStoreEntity sellerStoreEntity, Pageable pageable);
    Optional<StoreEntity> findByName(String name);

    Optional<StoreEntity> findByNameAndAddress(String name, AddressEntity address);
    Optional<StoreEntity> findByAddress_Id(Long id);
}

