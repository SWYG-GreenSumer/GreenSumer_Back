package org.swyg.greensumer.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.EventPostViewerEntity;

import java.util.Optional;

public interface EventPostViewerEntityRepository extends JpaRepository<EventPostViewerEntity, Long> {

    Optional<EventPostViewerEntity> findByEvent_IdAndUser_Id(Long eventId, Long userId);
}
