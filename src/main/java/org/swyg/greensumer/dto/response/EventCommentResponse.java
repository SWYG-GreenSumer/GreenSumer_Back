package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.EventComment;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentResponse {
    private Long id;
    private Long reviewPostId;
    private String username;
    private String content;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static EventCommentResponse fromEventComment(EventComment comment) {
        return new EventCommentResponse(
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
