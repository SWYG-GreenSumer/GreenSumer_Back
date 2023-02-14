package org.swyg.greensumer.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.EventPostLikeEntity;

public interface EventPostLikeRepository extends JpaRepository<EventPostLikeEntity, Long> {
}
