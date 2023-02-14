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
@Table(name = "event_likes")
@SQLDelete(sql = "UPDATE event_likes SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class EventPostLikeEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventPostEntity event;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public EventPostLikeEntity() {
    }

    private EventPostLikeEntity(EventPostEntity event, UserEntity user) {
        this.event = event;
        this.user = user;
    }

    public static EventPostLikeEntity of(EventPostEntity event, UserEntity user) {
        return new EventPostLikeEntity(event, user);
    }

    public void setReview(EventPostEntity event) {
        this.event = event;
    }
}
