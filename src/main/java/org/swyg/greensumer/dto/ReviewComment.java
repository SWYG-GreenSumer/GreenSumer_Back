package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewCommentEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewComment {
    private Long id;
    private String content;
    private User user;
    private ReviewPost post;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewComment fromEntity(ReviewCommentEntity entity) {
        return new ReviewComment(
                entity.getId(),
                entity.getContent(),
                User.fromEntity(entity.getUser()),
                ReviewPost.fromEntity(entity.getReviewPost()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
