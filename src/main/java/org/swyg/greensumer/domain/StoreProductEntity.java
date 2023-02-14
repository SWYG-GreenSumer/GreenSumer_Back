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
@Table(name = "store_product")
@SQLDelete(sql = "UPDATE store_product SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class StoreProductEntity extends DateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "store_id") private StoreEntity store;

    @ManyToOne @JoinColumn(name = "product_id") private ProductEntity product;

    public StoreProductEntity() {}

    private StoreProductEntity(StoreEntity store, ProductEntity product) {
        this.store = store;
        this.product= product;
    }

    public static StoreProductEntity of(StoreEntity store, ProductEntity product) {
        return new StoreProductEntity(store, product);
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
