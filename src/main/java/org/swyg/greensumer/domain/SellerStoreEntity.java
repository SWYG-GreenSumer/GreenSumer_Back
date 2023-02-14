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
@Table(name = "seller_store")
@SQLDelete(sql = "UPDATE seller_store SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class SellerStoreEntity extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "store_id") private StoreEntity store;        // 양방향

    @ManyToOne @JoinColumn(name = "seller_id") private UserEntity seller;       // 단방향

    public SellerStoreEntity() {}

    private SellerStoreEntity(StoreEntity store, UserEntity userEntity) {
        this.store = store;
        this.seller = userEntity;
    }

    public static SellerStoreEntity of(StoreEntity store, UserEntity seller) {
        return new SellerStoreEntity(store, seller);
    }

    public void setStore(StoreEntity storeEntity) {
        this.store = storeEntity;
    }

    public void setSeller(UserEntity seller) {
        this.seller = seller;
    }
}
