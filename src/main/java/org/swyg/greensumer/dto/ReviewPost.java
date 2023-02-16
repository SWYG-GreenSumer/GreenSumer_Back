package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPost {
    private Long id;
    private String title;
    private String content;
    private Integer views;
    private Integer likes;
    private String scope;
    private Set<ReviewComment> comments;
    private Set<Product> products;
    private User user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ReviewPost fromEntity(ReviewPostEntity entity){
        return new ReviewPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewer().size(),
                entity.getLikes().size(),
                entity.getScope(),
                entity.getComments().stream()
                        .map(ReviewComment::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getProducts().stream()
                        .map(Product::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                User.fromEntity(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}
