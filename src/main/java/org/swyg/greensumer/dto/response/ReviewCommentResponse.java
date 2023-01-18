package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.ReviewComment;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentResponse {
    private Integer id;
    private Integer reviewPost;
    private String username;
    private String content;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ReviewCommentResponse fromReviewComment(ReviewComment comment) {
        return new ReviewCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getUsername(),
                comment.getContent(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }
}
