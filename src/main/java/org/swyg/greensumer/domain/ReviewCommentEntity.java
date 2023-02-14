package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "review_comment")
@SQLDelete(sql = "UPDATE review_comment SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewCommentEntity extends CommentEntity {

    @ManyToOne(optional = false) @JoinColumn(name = "review_id")
    private ReviewPostEntity reviewPost;

    public ReviewCommentEntity() {}

    private ReviewCommentEntity(ReviewPostEntity reviewPost, UserEntity user, String content) {
        this.reviewPost = reviewPost;
        this.user = user;
        this.content = content;
    }

    public static ReviewCommentEntity of(ReviewPostEntity reviewPost, UserEntity user, String content) {
        return new ReviewCommentEntity(reviewPost, user, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewCommentEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

}
