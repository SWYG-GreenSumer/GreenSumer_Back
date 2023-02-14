package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.dto.EventPostWithComment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPostWithCommentResponse {
    private Long id;
    private String title;
    private String content;
    private Integer views;
    private Integer likes;
    private Set<ProductResponse> products;
    private UserResponse user;
    private Set<EventCommentResponse> reviewComments;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static EventPostWithCommentResponse fromEventPostWithComment(EventPostWithComment postWithComment) {
        return new EventPostWithCommentResponse(
                postWithComment.getId(),
                postWithComment.getTitle(),
                postWithComment.getContent(),
                postWithComment.getViews(),
                postWithComment.getLikes(),
                postWithComment.getProducts().stream()
                        .map(ProductResponse::fromProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                UserResponse.fromUser(postWithComment.getUser()),
                postWithComment.getReviewComments().stream()
                        .map(EventCommentResponse::fromEventComment)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                postWithComment.getEventStatus(),
                postWithComment.getStartedAt(),
                postWithComment.getEndedAt(),
                postWithComment.getRegisteredAt(),
                postWithComment.getUpdatedAt(),
                postWithComment.getDeletedAt()
        );
    }
}
