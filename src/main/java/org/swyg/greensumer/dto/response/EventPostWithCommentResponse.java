package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.dto.EventPostWithComment;

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
    private Long views;
    private Integer likes;
    private Set<EventPostWithProductResponse> products;
    private Set<ImageResponse> images;
    private UserResponse user;
    private Set<EventCommentResponse> reviewComments;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static EventPostWithCommentResponse fromEventPostWithComment(EventPostWithComment postWithComment) {
        return new EventPostWithCommentResponse(
                postWithComment.getId(),
                postWithComment.getTitle(),
                postWithComment.getContent(),
                postWithComment.getViews(),
                postWithComment.getLikes(),
                postWithComment.getProducts().stream()
                        .map(EventPostWithProductResponse::fromEventPostWithProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                postWithComment.getImages().stream()
                        .map(ImageResponse::fromImage)
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
