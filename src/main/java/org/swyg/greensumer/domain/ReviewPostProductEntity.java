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
@Table(name = "review_product")
@SQLDelete(sql = "UPDATE review_product SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ReviewPostProductEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "review_id") private ReviewPostEntity review;

    @ManyToOne @JoinColumn(name = "product_id") private ProductEntity product;

    public ReviewPostProductEntity() {}

    public void setReviewPost(ReviewPostEntity review) {
        this.review = review;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
