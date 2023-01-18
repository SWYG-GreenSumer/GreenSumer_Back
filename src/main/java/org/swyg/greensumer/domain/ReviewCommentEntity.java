package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "review_comment")
@SQLDelete(sql = "UPDATE review_comment SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private ReviewPostEntity reviewPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "updated_at")    private Timestamp updatedAt;
    @Column(name = "deleted_at")    private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }
    @PreUpdate  void updatedAt() { this.updatedAt = Timestamp.from(Instant.now()); }

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
