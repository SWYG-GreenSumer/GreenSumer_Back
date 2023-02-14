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
@Table(name = "product_image")
@SQLDelete(sql = "UPDATE product_image SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class ProductImageEntity extends ImageEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "product_id")
    private ProductEntity product;

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    @Builder
    private ProductImageEntity(UserEntity userEntity, String originFilename, String savedFilename, byte[] imageData) {
        this.userEntity = userEntity;
        this.originFilename = originFilename;
        this.savedFilename = savedFilename;
        this.imageData = imageData;
    }

    public static ProductImageEntity fromImageEntity(ImageEntity image) {
        return ProductImageEntity.builder()
                .originFilename(image.getOriginFilename())
                .savedFilename(image.savedFilename)
                .imageData(image.getImageData())
                .userEntity(image.userEntity)
                .build();
    }
}
