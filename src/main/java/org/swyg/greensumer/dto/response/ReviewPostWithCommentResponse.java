package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.ReviewPostWithComment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostWithCommentResponse {
    private Long id;
    private String title;
    private String content;
    private String rating;
    private Integer views;
    private Set<ProductResponse> products;
    private UserResponse user;
    private Set<ReviewCommentResponse> reviewComments;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewPostWithCommentResponse fromReviewPostWithComment(ReviewPostWithComment postWithComment) {
        return new ReviewPostWithCommentResponse(
                postWithComment.getId(),
                postWithComment.getTitle(),
                postWithComment.getContent(),
                postWithComment.getRating(),
                postWithComment.getViews(),
                postWithComment.getProducts().stream()
                        .map(ProductResponse::fromProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                UserResponse.fromUser(postWithComment.getUser()),
                postWithComment.getReviewComments().stream()
                        .map(ReviewCommentResponse::fromReviewComment)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                postWithComment.getRegisteredAt(),
                postWithComment.getUpdatedAt(),
                postWithComment.getDeletedAt()
        );
    }
}
