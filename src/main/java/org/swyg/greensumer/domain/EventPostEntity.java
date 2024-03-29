package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Getter
@Entity
@Table(name = "event_post")
@SQLDelete(sql = "UPDATE event_post SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class EventPostEntity extends PostEntity {

    @ToString.Exclude
    @OrderBy("registeredAt DESC")
    @OneToMany(mappedBy = "eventPost")
    private Set<EventCommentEntity> comments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event", orphanRemoval = true, cascade = {CascadeType.ALL})
    private Set<EventImageEntity> images = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<EventPostLikeEntity> likes = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<EventPostProductEntity> eventPostProductEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.ORDINAL)
    private EventStatus eventStatus;

    private LocalDateTime started_at;
    private LocalDateTime ended_at;

    protected EventPostEntity() {
    }

    private EventPostEntity(UserEntity user, String title, String content, LocalDateTime started_at, LocalDateTime ended_at, EventStatus status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.started_at = started_at;
        this.ended_at = ended_at;
        this.eventStatus = status;
    }

    @Builder
    public static EventPostEntity of(UserEntity user, String title, String content, LocalDateTime started_at, LocalDateTime ended_at, EventStatus status) {
        return new EventPostEntity(user, title, content, started_at, ended_at, status);
    }

    public void addImages(Collection<EventImageEntity> images) {
        images.forEach(e -> e.setEvent(this));
        this.images.clear();
        this.images.addAll(images);
    }

    public void addViewer() {
        this.views++;
    }

    public void setEventPostProductEntities(Collection<EventPostProductEntity> eventPostProductEntities) {
        eventPostProductEntities.forEach(e -> e.setEvent(this));
        this.eventPostProductEntities.clear();
        this.eventPostProductEntities.addAll(eventPostProductEntities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventPostEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void updateEventPost(List<EventPostProductEntity> eventPostProductEntities, String title, String content, LocalDateTime started_at, LocalDateTime ended_at, EventStatus eventStatus) {
        this.title = title;
        this.content = content;
        this.started_at = started_at;
        this.ended_at = ended_at;
        this.eventStatus = eventStatus;

        setEventPostProductEntities(eventPostProductEntities);
    }

    public void clear() {
        this.images.clear();
        this.eventPostProductEntities.clear();
    }

    public void addLikes(EventPostLikeEntity eventPostLikeEntity) {
        if (this.likes.remove(eventPostLikeEntity)) {
            return;
        }

        eventPostLikeEntity.setReview(this);
        this.likes.add(eventPostLikeEntity);
    }
}
