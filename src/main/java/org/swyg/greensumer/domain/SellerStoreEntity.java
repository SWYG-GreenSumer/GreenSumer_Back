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
@Table(name = "seller_store")
@SQLDelete(sql = "UPDATE seller_store SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class SellerStoreEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter @ManyToOne @JoinColumn(name = "store_id") private StoreEntity store;        // 양방향

    @Setter @ManyToOne @JoinColumn(name = "seller_id") private UserEntity seller;       // 단방향

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "deleted_at")    private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }

    public SellerStoreEntity() {}

    private SellerStoreEntity(StoreEntity store, UserEntity userEntity) {
        this.store = store;
        this.seller = userEntity;
    }

    public static SellerStoreEntity of(StoreEntity store, UserEntity seller) {
        return new SellerStoreEntity(store, seller);
    }
}
