package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "review_post")
@SQLDelete(sql = "UPDATE review_post SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostEntity extends PostEntity {

    @ToString.Exclude
    @OrderBy("registeredAt DESC")
    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private Set<ReviewCommentEntity> comments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "review", orphanRemoval = true, cascade = {CascadeType.ALL})
    private Set<ReviewImageEntity> images = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "review", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<ReviewPostLikeEntity> likes = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "review", cascade = {CascadeType.ALL}, orphanRemoval = false)
    private Set<ReviewPostProductEntity> postProductsEntities = new LinkedHashSet<>();

    private String rating;

    public ReviewPostEntity() {
    }

    private ReviewPostEntity(UserEntity user, String title, String content, String rating) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.views = 0L;
    }

    public static ReviewPostEntity of(UserEntity user, String title, String content, String rating) {
        return new ReviewPostEntity(user, title, content, rating);
    }

    public void setProducts(Collection<ReviewPostProductEntity> products) {
        postProductsEntities.forEach(e -> e.setReviewPost(this));
        this.postProductsEntities.clear();
        this.postProductsEntities.addAll(products);
    }

    public void addImages(Collection<ReviewImageEntity> images) {
        images.forEach(e -> e.setReview(this));
        this.images.clear();
        this.images.addAll(images);
    }

    public void addViewer() {
        this.views++;
    }

    public void addLikes(ReviewPostLikeEntity reviewPostLikeEntity) {
        if (this.likes.remove(reviewPostLikeEntity)) {
            return;
        }

        reviewPostLikeEntity.setReview(this);
        this.likes.add(reviewPostLikeEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewPostEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void updateReviewPost(String title, String content, String rating, Collection<ReviewPostProductEntity> reviewPostProductsEntities) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.setProducts(reviewPostProductsEntities);
    }

    public void clear() {
        this.postProductsEntities.clear();
        this.images.clear();
    }
}
