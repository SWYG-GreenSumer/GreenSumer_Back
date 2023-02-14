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
@Table(name = "review_viewer")
@SQLDelete(sql = "UPDATE review_viewer SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostViewerEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "review_id") private ReviewPostEntity review;       // 양방향
    @ManyToOne @JoinColumn(name = "user_id") private UserEntity user;         // 단방향

    public ReviewPostViewerEntity() {}

    private ReviewPostViewerEntity(ReviewPostEntity reviewPostEntity, UserEntity userEntity) {
        this.review = reviewPostEntity;
        this.user = userEntity;
    }

    public static ReviewPostViewerEntity of(ReviewPostEntity reviewPostEntity, UserEntity userEntity) {
        return new ReviewPostViewerEntity(reviewPostEntity, userEntity);
    }

    public void setReview(ReviewPostEntity reviewPostEntity) {
        this.review = reviewPostEntity;
    }
}
