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
@Table(name = "review_likes")
@SQLDelete(sql = "UPDATE review_likes SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostLikeEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "review_id") private ReviewPostEntity review;
    @ManyToOne @JoinColumn(name = "user_id") private UserEntity user;

    public ReviewPostLikeEntity() {}

    private ReviewPostLikeEntity(ReviewPostEntity review, UserEntity user) {
        this.review = review;
        this.user = user;
    }

    public static ReviewPostLikeEntity of(ReviewPostEntity review, UserEntity user){
        return new ReviewPostLikeEntity(review, user);
    }

    public void setReview(ReviewPostEntity reviewPostEntity) {
        this.review = reviewPostEntity;
    }
}
