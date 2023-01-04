package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.swyg.greensumer.domain.constant.StoreType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Entity
@Table(name = "\"store\"", indexes = {
        @Index(name = "store_name_idx", columnList = "name", unique = true)
})
@SQLDelete(sql = "UPDATE \"store\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter @ManyToOne
    @JoinColumn(name = "seller_id")
    private UserEntity user;

    @Setter @Column(name = "name", length = 30) private String name;

    @Setter @Column(name = "description", columnDefinition = "TEXT") private String description;

    @Setter private String address;
    @Setter private String hours;
    @Setter private String lat;
    @Setter private String lng;
    @Setter private String logo;

    @Setter @Column(name = "type") @Enumerated(EnumType.STRING) private StoreType storeType;

    @Column(name = "registered_at") private Timestamp registeredAt;
    @Column(name = "updated_at") private Timestamp updatedAt;
    @Column(name = "deleted_at") private Timestamp deletedAt;

    @PrePersist
    void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }

    @PreUpdate
    void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}

    protected StoreEntity(){}

    public static StoreEntity of(UserEntity user, String name, String description, String address, String hours, String lat, String lng, String logo, StoreType storeType) {
       StoreEntity storeEntity = new StoreEntity();
       storeEntity.setUser(user);
       storeEntity.setName(name);
       storeEntity.setDescription(description);
       storeEntity.setAddress(address);
       storeEntity.setHours(hours);
       storeEntity.setLat(lat);
       storeEntity.setLng(lng);
       storeEntity.setLogo(logo);
       storeEntity.setStoreType(storeType);

       return storeEntity;
    }
}
