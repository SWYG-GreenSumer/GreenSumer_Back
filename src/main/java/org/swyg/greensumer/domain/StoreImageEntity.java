package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "store_image")
@SQLDelete(sql = "UPDATE store_image SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class StoreImageEntity extends ImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "store_id")
    private StoreEntity store;

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    @Builder
    private StoreImageEntity(UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        this.userEntity = userEntity;
        this.originFilename = originFilename;
        this.savedFilename = savedFilename;
        this.imageData = imageData;
    }

    public static StoreImageEntity fromImageEntity(ImageEntity image) {
        return StoreImageEntity.builder()
                .originFilename(image.getOriginFilename())
                .savedFilename(image.savedFilename)
                .imageData(image.getImageData())
                .userEntity(image.userEntity)
                .build();
    }
}
