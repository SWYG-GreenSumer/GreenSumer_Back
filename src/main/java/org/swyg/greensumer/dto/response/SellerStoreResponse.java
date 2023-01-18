package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.SellerStore;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerStoreResponse {
    private StoreResponse storeResponse;
    private UserResponse userResponse;

    public static SellerStoreResponse fromSellerStore(SellerStore sellerStore) {
        return new SellerStoreResponse(
                StoreResponse.fromStore(sellerStore.getStore()),
                UserResponse.fromUser(sellerStore.getSeller())
        );
    }
}
