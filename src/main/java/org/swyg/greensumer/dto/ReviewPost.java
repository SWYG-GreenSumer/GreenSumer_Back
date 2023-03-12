package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPost {
    private Long id;
    private String title;
    private String content;
    private Long views;
    private Integer likes;
    private String scope;
    private Set<ReviewComment> comments;
    private Set<Product> products;
    private Set<Image> images;
    private User user;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewPost fromEntity(ReviewPostEntity entity){
        return new ReviewPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViews(),
                entity.getLikes().size(),
                entity.getRating(),
                entity.getComments().stream()
                        .map(ReviewComment::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getPostProductsEntities().stream()
                        .map(ReviewPostWithProduct::fromEntity)
                        .map(ReviewPostWithProduct::getProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getImages().stream()
                        .map(Image::fromReviewImageEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                User.fromEntity(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}
