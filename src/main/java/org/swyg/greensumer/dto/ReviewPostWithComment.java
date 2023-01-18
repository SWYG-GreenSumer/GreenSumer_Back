package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostWithComment {
    private Integer id;
    private String title;
    private String content;
    private Product product;
    private User user;
    private Set<ReviewComment> reviewComments;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ReviewPostWithComment fromEntity(ReviewPostEntity entity) {
        return new ReviewPostWithComment(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                Product.fromEntity(entity.getProduct()),
                User.fromEntity(entity.getUser()),
                entity.getComments().stream()
                        .map(ReviewComment::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
