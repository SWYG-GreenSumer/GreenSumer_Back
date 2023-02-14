package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "event_post_viewer")
@SQLDelete(sql = "UPDATE event_post_viewer SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class EventPostViewerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "event_id") private EventPostEntity event;
    @ManyToOne @JoinColumn(name = "user_id") private UserEntity user;

    public EventPostViewerEntity() {}

    @Builder
    private EventPostViewerEntity(EventPostEntity eventPostEntity, UserEntity userEntity) {
        this.event = eventPostEntity;
        this.user = userEntity;
    }

    public static EventPostViewerEntity of(EventPostEntity eventPostEntity, UserEntity userEntity) {
        return new EventPostViewerEntity(eventPostEntity, userEntity);
    }

    public void setEvent(EventPostEntity eventPostEntity) {
        this.event = eventPostEntity;
    }
}
