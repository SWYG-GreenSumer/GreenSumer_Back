package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.ReviewPost;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostResponse {
    private Integer id;
    private UserResponse user;
    private ProductResponse product;
    private String title;
    private String content;
    private String hashtag;
    private String imagePath;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ReviewPostResponse fromReviewPost(ReviewPost reviewPost) {
        return new ReviewPostResponse(
                reviewPost.getId(),
                UserResponse.fromUser(reviewPost.getUser()),
                ProductResponse.fromProduct(reviewPost.getProduct()),
                reviewPost.getTitle(),
                reviewPost.getContent(),
                reviewPost.getHashtag(),
                reviewPost.getImagePath(),
                reviewPost.getRegisteredAt(),
                reviewPost.getUpdatedAt(),
                reviewPost.getDeletedAt()
        );
    }
}
