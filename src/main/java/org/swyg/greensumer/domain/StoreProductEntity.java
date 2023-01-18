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
@Table(name = "store_product")
@SQLDelete(sql = "UPDATE store_product SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class StoreProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter @ManyToOne @JoinColumn(name = "store_id") private StoreEntity store;

    @Setter @ManyToOne @JoinColumn(name = "product_id") private ProductEntity product;

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "deleted_at")    private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }

    public StoreProductEntity() {}

    private StoreProductEntity(StoreEntity store, ProductEntity product) {
        this.store = store;
        this.product= product;
    }

    public static StoreProductEntity of(StoreEntity store, ProductEntity product) {
        return new StoreProductEntity(store, product);
    }
}
