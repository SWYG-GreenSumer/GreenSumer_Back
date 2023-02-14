package org.swyg.greensumer.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.EventPostEntity;

public interface EventPostEntityRepository extends JpaRepository<EventPostEntity, Long> {

    Page<EventPostEntity> findAllByUser_Id(Long userId, Pageable pageable);
}
