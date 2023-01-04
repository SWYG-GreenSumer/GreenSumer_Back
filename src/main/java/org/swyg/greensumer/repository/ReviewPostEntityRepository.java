package org.swyg.greensumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;

public interface ReviewPostEntityRepository extends JpaRepository<ReviewPostEntity, Integer> {

    Page<ReviewPostEntity> findAllByUser(UserEntity user, Pageable pageable);
}
