package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.SellerStoreEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerStore {
    private Integer id;
    private Store store;
    private User seller;

    public static SellerStore fromEntity(SellerStoreEntity entity) {
        return new SellerStore(
                entity.getId(),
                Store.fromEntity(entity.getStore()),
                User.fromEntity(entity.getSeller())
        );
    }
}
