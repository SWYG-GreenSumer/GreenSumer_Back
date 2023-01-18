package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Table(name = "review_post")
@SQLDelete(sql = "UPDATE review_post SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostEntity {
    @Setter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ToString.Exclude
    @OrderBy("registeredAt DESC")
    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private final Set<ReviewCommentEntity> comments = new LinkedHashSet<>();

    @Setter @Column(name = "title", length = 50) private String title;
    @Setter @Column(name = "content", columnDefinition = "TEXT")  private String content;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = {CascadeType.ALL})
    private final Set<ImageEntity> images = new LinkedHashSet<>();

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }
    @PreUpdate void updatedAt() { this.updatedAt = Timestamp.from(Instant.now()); }

    public ReviewPostEntity() {}

    private ReviewPostEntity (ProductEntity product, UserEntity user, String title, String content) {
       this.product = product;
       this.user = user;
       this.title = title;
       this.content = content;
    }

    public static ReviewPostEntity of(ProductEntity product, UserEntity user, String title, String content) {
        return new ReviewPostEntity(product, user, title, content);
    }

    public void addImage(ImageEntity image) {
        image.setReview(this);
        this.images.add(image);
    }

    public void addImages(Collection<ImageEntity> images) {
        images.forEach(e -> e.setReview(this));
        this.images.addAll(images);
    }

    public void deleteImage(ImageEntity image) {
        image.setReview(this);
        this.images.remove(image);
    }

    public void deleteImages(Collection<ImageEntity> images) {
        images.forEach(e -> e.setReview(this));
        this.images.retainAll(images);
    }

    public void clearImages() {
        this.images.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof ReviewPostEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

}
