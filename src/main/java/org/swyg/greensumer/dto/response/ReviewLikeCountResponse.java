package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.dto.ReviewPost;

@Getter
@Builder
public class ReviewLikeCountResponse {
    private long reviewId;
    private int like;

    public static ReviewLikeCountResponse fromReviewPost(ReviewPost reviewPost){
        return ReviewLikeCountResponse.builder()
                .reviewId(reviewPost.getId())
                .like(reviewPost.getLikes())
                .build();
    }
}
