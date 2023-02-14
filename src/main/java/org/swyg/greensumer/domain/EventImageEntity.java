package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "event_image")
@SQLDelete(sql = "UPDATE event_image SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class EventImageEntity extends ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventPostEntity event;

    public void setEvent(EventPostEntity event) {
        this.event = event;
    }

    @Builder
    private EventImageEntity(UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        this.userEntity = userEntity;
        this.originFilename = originFilename;
        this.savedFilename = savedFilename;
        this.imageData = imageData;
    }

    public static EventImageEntity fromImageEntity(ImageEntity image) {
        return EventImageEntity.builder()
                .originFilename(image.getOriginFilename())
                .savedFilename(image.savedFilename)
                .imageData(image.getImageData())
                .userEntity(image.userEntity)
                .build();
    }
}
