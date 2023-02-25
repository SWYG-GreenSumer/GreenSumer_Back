package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.StoreProductEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreProduct {
    private Long id;
    private Store store;
    private Product product;
    private LocalDateTime registeredAt;
    private LocalDateTime deletedAt;

    public static StoreProduct fromEntity(StoreProductEntity entity) {
        return new StoreProduct(
                entity.getId(),
                Store.fromEntity(entity.getStore()),
                Product.fromEntity(entity.getProduct()),
                entity.getRegisteredAt(),
                entity.getDeletedAt()
        );
    }
}
