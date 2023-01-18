package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.StoreProduct;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductResponse {
    private StoreResponse storeResponse;
    private ProductResponse productResponse;

    public static StoreProductResponse fromStoreProduct(StoreProduct storeProduct){
        return new StoreProductResponse(
                StoreResponse.fromStore(storeProduct.getStore()),
                ProductResponse.fromProduct(storeProduct.getProduct())
        );
    }
}
