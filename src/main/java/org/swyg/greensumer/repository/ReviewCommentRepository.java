package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.ReviewCommentEntity;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Integer> {
}
