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
@Table(name = "review_image")
@SQLDelete(sql = "UPDATE review_image SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class ReviewImageEntity extends ImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "review_id")
    private ReviewPostEntity review;

    public void setReview(ReviewPostEntity review) {
        this.review = review;
    }

    @Builder
    private ReviewImageEntity(UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        this.userEntity = userEntity;
        this.originFilename = originFilename;
        this.savedFilename = savedFilename;
        this.imageData = imageData;
    }

    public static ReviewImageEntity fromImageEntity(ImageEntity image) {
        return ReviewImageEntity.builder()
                .originFilename(image.getOriginFilename())
                .savedFilename(image.savedFilename)
                .imageData(image.getImageData())
                .userEntity(image.userEntity)
                .build();
    }
}
