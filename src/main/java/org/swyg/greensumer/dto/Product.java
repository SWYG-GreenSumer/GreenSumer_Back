package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ProductEntity;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private int price;
    private int stock;
    private String description;
    private String image;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Product fromEntity(ProductEntity entity){
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getStock(),
                entity.getDescription(),
                entity.getImage(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
