package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.EventCommentEntity;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventComment {
    private Long id;
    private String content;
    private User user;
    private EventPost post;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static EventComment fromEntity(EventCommentEntity entity) {
        return new EventComment(
                entity.getId(),
                entity.getContent(),
                User.fromEntity(entity.getUser()),
                EventPost.fromEntity(entity.getEventPost()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
