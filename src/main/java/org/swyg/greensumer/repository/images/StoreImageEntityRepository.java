package org.swyg.greensumer.repository.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.StoreImageEntity;

import java.util.List;

public interface StoreImageEntityRepository extends JpaRepository<StoreImageEntity, Long> {
    List<StoreImageEntity> findAllByIdIn(List<Long> ids);

}
