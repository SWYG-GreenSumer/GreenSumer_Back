package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.EventPostWithProduct;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPostWithProductResponse {
    private ProductResponse product;

    public static EventPostWithProductResponse fromEventPostWithProduct(EventPostWithProduct eventPostWithProduct) {
        return EventPostWithProductResponse.builder()
                .product(ProductResponse.fromProduct(eventPostWithProduct.getProduct()))
                .build();
    }
}
