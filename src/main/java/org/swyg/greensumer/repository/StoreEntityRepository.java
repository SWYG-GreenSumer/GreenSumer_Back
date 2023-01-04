package org.swyg.greensumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.UserEntity;

import java.util.Optional;

public interface StoreEntityRepository extends JpaRepository<StoreEntity, Integer> {
    boolean existsByName(String name);

    Page<StoreEntity> findAllByUser(UserEntity user, Pageable pageable);

    Optional<StoreEntity> findByNameAndId(String name, Integer id);
}
