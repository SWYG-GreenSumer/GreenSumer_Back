package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.dto.ReviewPost;

import java.sql.Timestamp;

@Builder
@Getter
public class ReviewPostsResponse {
    private Long id;
    private String title;
    private String nickname;
    private int views;
    private int likes;
    private int comments;
    private Timestamp registeredAt;
    private Timestamp updatedAt;

    public static ReviewPostsResponse fromReviewPost(ReviewPost reviewPost) {
        return ReviewPostsResponse.builder()
                .title(reviewPost.getTitle())
                .nickname(reviewPost.getUser().getNickname())
                .views(reviewPost.getViews())
                .likes(reviewPost.getLikes())
                .comments(reviewPost.getComments().size())
                .build();
    }
}
