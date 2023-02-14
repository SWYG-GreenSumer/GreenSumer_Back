package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.dto.EventPost;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPostResponse {
    private Long id;
    private UserResponse user;
    private Set<ProductResponse> product;
    private String title;
    private String content;
    private Integer views;
    private Integer likes;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static EventPostResponse fromEventPost(EventPost eventPost) {
        return new EventPostResponse(
                eventPost.getId(),
                UserResponse.fromUser(eventPost.getUser()),
                eventPost.getProducts().stream()
                        .map(ProductResponse::fromProduct)
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
