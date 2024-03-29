package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.dto.EventPost;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPostResponse {
    private Long id;
    private UserResponse user;
    private Set<EventPostWithProductResponse> product;
    private Set<ImageResponse> images;
    private String title;
    private String content;
    private Long views;
    private Integer likes;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static EventPostResponse fromEventPost(EventPost eventPost) {
        return new EventPostResponse(
                eventPost.getId(),
                UserResponse.fromUser(eventPost.getUser()),
                eventPost.getProducts().stream()
                        .map(EventPostWithProductResponse::fromEventPostWithProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                eventPost.getImages().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet()),
                eventPost.getTitle(),
                eventPost.getContent(),
                eventPost.getViews(),
                eventPost.getLikes(),
                eventPost.getEventStatus(),
                eventPost.getStartedAt(),
                eventPost.getEndedAt(),
                eventPost.getRegisteredAt(),
                eventPost.getUpdatedAt(),
                eventPost.getDeletedAt()
        );
    }
}
