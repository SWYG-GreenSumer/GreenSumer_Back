package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.SellerStoreEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerStore {
    private Store store;

    public static SellerStore fromEntity(SellerStoreEntity entity) {
        return new SellerStore(
                Store.fromEntity(entity.getStore())
        );
    }
}
