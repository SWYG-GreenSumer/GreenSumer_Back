package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.StoreType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Table(name = "store")
@SQLDelete(sql = "UPDATE store SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class StoreEntity {

    @Setter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter @Column(name = "name", length = 30) private String name;

    @Setter @Column(name = "description", columnDefinition = "TEXT") private String description;

    @Setter @Column(name = "type") @Enumerated(EnumType.STRING) private StoreType storeType;

    @Setter @ToString.Exclude @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true) @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Setter private String hours;

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<SellerStoreEntity> sellerStores = new LinkedHashSet<>();

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<StoreProductEntity> storeProducts = new LinkedHashSet<>();

    @ToString.Exclude @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", orphanRemoval = true, cascade = {CascadeType.ALL} )
    private Set<ImageEntity> logos = new LinkedHashSet<>();

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "updated_at") private Timestamp updatedAt;
    @Column(name = "deleted_at") private Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }
    @PreUpdate  void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}

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

    // image
    public void addImage(ImageEntity image) {
        image.setStore(this);
        this.logos.add(image);
    }

    public void addImages(Collection<ImageEntity> images) {
        images.forEach(e -> e.setStore(this));
        this.logos.addAll(images);
    }

    public void deleteImage(ImageEntity image) {
        this.logos.remove(image);
    }

    public void deleteImages(Collection<ImageEntity> images) {
        this.logos.retainAll(images);
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
}
