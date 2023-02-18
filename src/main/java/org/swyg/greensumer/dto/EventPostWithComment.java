package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.EventPostEntity;
import org.swyg.greensumer.domain.constant.EventStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPostWithComment {
    private Long id;
    private String title;
    private String content;
    private Integer views;
    private Integer likes;
    private Set<Product> products;
    private User user;
    private Set<EventComment> reviewComments;
    private EventStatus eventStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static EventPostWithComment fromEntity(EventPostEntity entity) {
        return new EventPostWithComment(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewer().size(),
                entity.getLikes().size(),
                entity.getProducts().stream()
                        .map(Product::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                User.fromEntity(entity.getUser()),
                entity.getComments().stream()
                        .map(EventComment::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getEventStatus(),
                entity.getStarted_at(),
                entity.getEnded_at(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
