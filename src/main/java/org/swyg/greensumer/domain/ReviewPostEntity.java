package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Entity
@Table(name = "\"review_post\"")
@SQLDelete(sql = "UPDATE \"review_post\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // TODO: 후기 게시글은 여러 개에 아이템을 선택할 수 있어야한다.
    @Setter @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Setter @Column(name = "title", length = 50)                 private String title;
    @Setter @Column(name = "content", columnDefinition = "TEXT") private String content;

    // TODO : 이미지 또한 엔티티를 분리하여 관리할 필요가 있음. 이미지 제목, 이미지 경로, 작성자 정보가 필요하기 때문
    @Setter @Column(name = "hashtag", length = 50)  private String hashtag;
    @Setter @Column(name = "imagePath", length = 500)  private String imagePath;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }

    @PreUpdate
    void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}

    protected ReviewPostEntity() {}

    public static ReviewPostEntity of(ProductEntity productEntity, UserEntity user, String title, String content, String hashtag, String imagePath) {
        ReviewPostEntity reviewPostEntity = new ReviewPostEntity();
        reviewPostEntity.setUser(user);
        reviewPostEntity.setProductEntity(productEntity);
        reviewPostEntity.setTitle(title);
        reviewPostEntity.setContent(content);
        reviewPostEntity.setHashtag(hashtag);
        reviewPostEntity.setImagePath(imagePath);

        return reviewPostEntity;
    }

}
