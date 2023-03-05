package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.dto.ReviewPost;

@Builder
@Getter
public class ReviewNewPostResponse {
    private String title;
    private String nickname;
    private String scope;
    private Long views;
    private int likes;

    public static ReviewNewPostResponse fromReviewPost(ReviewPost reviewPost){
        return ReviewNewPostResponse.builder()
                .title(reviewPost.getTitle())
                .nickname(reviewPost.getUser().getNickname())
                .scope(reviewPost.getScope())
                .views(reviewPost.getViews())
                .likes(reviewPost.getLikes())
                .build();
    }
}
