package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostEntity;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPost {
    private Integer id;
    private String title;
    private String content;
    private String hashtag;
    private String imagePath;
    private Product product;
    private User user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ReviewPost fromEntity(ReviewPostEntity entity){
        return new ReviewPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getImagePath(),
                Product.fromEntity(entity.getProductEntity()),
                User.fromEntity(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}
