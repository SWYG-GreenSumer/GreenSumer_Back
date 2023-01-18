package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.UserEntity;

import java.util.List;

public interface ImageEntityRepository extends JpaRepository<ImageEntity, Integer> {

    List<ImageEntity> findAllByUserEntity(UserEntity user);

    List<ImageEntity> findAllByIdIn(List<Integer> id);

    void deleteAllByProduct(ProductEntity productEntity);
}
