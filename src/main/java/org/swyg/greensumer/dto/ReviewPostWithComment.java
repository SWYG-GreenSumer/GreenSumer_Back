package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

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
    private String rating;
    private Long views;
    private Set<ReviewPostWithProduct> products;
    private Set<Image> images;
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
                entity.getRating(),
                entity.getViews(),
                entity.getPostProductsEntities().stream()
                        .map(ReviewPostWithProduct::getProductFromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getImages().stream()
                        .map(Image::fromReviewImageEntity)
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
