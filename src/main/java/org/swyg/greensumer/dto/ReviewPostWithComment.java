package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostWithComment {
    private Long id;
    private String title;
    private String content;
    private String scope;
    private Integer views;
    private Set<Product> products;
    private User user;
    private Set<ReviewComment> reviewComments;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewPostWithComment fromEntity(ReviewPostEntity entity) {
        return new ReviewPostWithComment(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getScope(),
                entity.getViewer().size(),
                entity.getProducts().stream()
                        .map(Product::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
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
