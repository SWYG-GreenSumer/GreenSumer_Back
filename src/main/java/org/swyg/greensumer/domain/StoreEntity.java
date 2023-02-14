package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.StoreType;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "store")
@SQLDelete(sql = "UPDATE store SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class StoreEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30) private String name;

    @Column(name = "description", columnDefinition = "TEXT") private String description;

    @Column(name = "type") @Enumerated(EnumType.STRING) private StoreType storeType;

    @ToString.Exclude @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true) @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Column(name = "hours") private String hours;

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<SellerStoreEntity> sellerStores = new LinkedHashSet<>();

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<StoreProductEntity> storeProducts = new LinkedHashSet<>();

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", orphanRemoval = true, cascade = {CascadeType.ALL} )
    private Set<StoreImageEntity> logos = new LinkedHashSet<>();

    public StoreEntity(){}

    private StoreEntity(String name, String description, StoreType storeType, AddressEntity address, String hours) {
        this.name = name;
        this.description = description;
        this.storeType = storeType;
        this.address = address;
        this.hours = hours;
    }

    public static StoreEntity of(String name, String description, StoreType storeType, AddressEntity address, String hours) {
        return new StoreEntity(name, description, storeType, address, hours);
    }

    // SellerStore
    public void addSellerStore(SellerStoreEntity sellerStoreEntity){
        sellerStoreEntity.setStore(this);
        this.sellerStores.add(sellerStoreEntity);
    }

    public void addSellerStores(Collection<SellerStoreEntity> sellerStoreEntities){
        sellerStoreEntities.forEach(e -> e.setStore(this));
        this.sellerStores.addAll(sellerStoreEntities);
    }

    public void removeSellerStore(SellerStoreEntity sellerStoreEntity){
        this.sellerStores.remove(sellerStoreEntity);
    }

    public void clearSellerStores(){
        this.sellerStores.clear();
    }

    // StoreProduct
    public void addStoreProduct(StoreProductEntity storeProductEntity){
        storeProductEntity.setStore(this);
        this.storeProducts.add(storeProductEntity);
    }

    public void addStoreProducts(Collection<StoreProductEntity> storeProductEntities){
        storeProductEntities.forEach(e -> e.setStore(this));
        this.storeProducts.addAll(storeProductEntities);
    }

    public void removeStoreProduct(StoreProductEntity storeProductEntity){
        this.storeProducts.remove(storeProductEntity);
    }

    public void clearStoreProducts(){
        this.storeProducts.clear();
    }

    public void addImages(Collection<StoreImageEntity> images) {
        images.forEach(e -> e.setStore(this));
        this.logos.clear();
        this.logos.addAll(images);
    }

    public void deleteImages(Collection<StoreImageEntity> images) {
        this.logos.removeAll(images);
    }

    public void clearImages() {
        this.logos.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoreEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void updateStore(String type, String description, String hours) {
        this.storeType = StoreType.valueOf(type);
        this.description = description;
        this.hours = hours;
    }

    public void updateAddress(AddressEntity address) {
        this.address = address;
    }
}
