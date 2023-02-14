package org.swyg.greensumer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
@Table(name = "product", indexes = {
        @Index(name = "product_name_idx", columnList = "name")
})
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class ProductEntity extends DateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private Set<StoreProductEntity> storeProducts = new LinkedHashSet<>();

    @JsonIgnore @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "review_id")
    private ReviewPostEntity reviewPost;

    @JsonIgnore @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "event_id")
    private EventPostEntity eventPost;

    @Column(name = "name", length = 50) private String name;

    @Column(name = "price") private int price;
    @Column(name = "stock") private int stock;

    @Column(name = "description", columnDefinition = "TEXT") private String description;

    @OrderBy("id asc")  // 아이디 순으로 정렬
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<ProductImageEntity> images = new LinkedHashSet<>();

    public void setReviewPost(ReviewPostEntity reviewPost) {
        this.reviewPost = reviewPost;
    }

    public void setEventPost(EventPostEntity eventPost) {
        this.eventPost = eventPost;
    }

    public void addStoreProduct(StoreProductEntity storeProductEntity){
        storeProductEntity.setProduct(this);
        this.storeProducts.add(storeProductEntity);
    }

    public void clearStoreProduct(){
        this.storeProducts.clear();
    }

    public void addImages(Collection<ProductImageEntity> images) {
        images.forEach(e -> e.setProduct(this));
        this.images.clear();
        this.images.addAll(images);
    }

    public void deleteImages(Collection<ProductImageEntity> images) {
        images.forEach(e -> e.setProduct(this));
        this.images.removeAll(images);
    }

    public void clearImages() {
        this.images.clear();
    }

    public ProductEntity(){}

    private ProductEntity(String name, int price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public static ProductEntity of(String name, int price, int stock, String description) {
        return new ProductEntity(name, price, stock, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void updateProductInfo(String name, String description, int price, int stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }
}
