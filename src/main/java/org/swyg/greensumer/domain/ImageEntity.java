package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.ImageType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@ToString
@Setter
@Getter
@Table(name = "image")
@SQLDelete(sql = "UPDATE image SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private ImageType imageType;

    @OneToOne @JoinColumn(name = "user_id")
    private UserEntity userEntity;  // 업로드 한 유저

    @ManyToOne @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne @JoinColumn(name = "review_post_id")
    private ReviewPostEntity review;

    @ManyToOne @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(columnDefinition = "TEXT")
    private String originFilename;

    @Column(columnDefinition = "TEXT")
    private String savedFilename;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagedata", length = 16_000_000)
    private byte[] imageData;

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "updated_at") private Timestamp updatedAt;
    @Column(name = "deleted_at") private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }
    @PreUpdate  void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}

    public ImageEntity() {}

    private ImageEntity(ImageType imageType, UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        this.imageType = imageType;
        this.userEntity = userEntity;
        this.originFilename = originFilename;
        this.savedFilename = savedFilename;
        this.imageData = imageData;
    }

    public static ImageEntity of(ImageType imageType, UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        return new ImageEntity(imageType, userEntity, originFilename, savedFilename, imageData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
