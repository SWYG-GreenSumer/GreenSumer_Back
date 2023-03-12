package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.EventPostEntity;
import org.swyg.greensumer.domain.constant.EventStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPost {
    private Long id;
    private String title;
    private String content;
    private Long views;
    private Integer likes;
    private Set<EventPostWithProduct> products;
    private Set<Image> images;
    private User user;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static EventPost fromEntity(EventPostEntity entity) {
        return new EventPost(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViews(),
                entity.getLikes().size(),
                entity.getEventPostProductEntities().stream()
                        .map(EventPostWithProduct::getProductFromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getImages().stream()
                        .map(Image::fromEventImageEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                User.fromEntity(entity.getUser()),
                entity.getEventStatus(),
                entity.getStarted_at(),
                entity.getEnded_at(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}
