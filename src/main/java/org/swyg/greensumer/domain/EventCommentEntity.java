package org.swyg.greensumer.domain;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Entity
@Table(name = "event_comment")
@SQLDelete(sql = "UPDATE event_comment SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class EventCommentEntity extends CommentEntity {

    @ManyToOne(optional = false) @JoinColumn(name = "event_id")
    private EventPostEntity eventPost;

    public EventCommentEntity() {}

    @Builder
    private EventCommentEntity(EventPostEntity eventPost, UserEntity user, String content) {
        this.eventPost = eventPost;
        this.user = user;
        this.content = content;
    }

    public static EventCommentEntity of(EventPostEntity eventPost, UserEntity user, String content) {
        return new EventCommentEntity(eventPost, user, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventCommentEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
