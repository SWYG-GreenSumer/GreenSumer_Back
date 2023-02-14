package org.swyg.greensumer.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.EventCommentEntity;

public interface EventCommentEntityRepository extends JpaRepository<EventCommentEntity, Long> {
}
