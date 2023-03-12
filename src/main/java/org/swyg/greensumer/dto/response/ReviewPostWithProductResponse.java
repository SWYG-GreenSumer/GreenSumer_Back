package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.ReviewPostWithProduct;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPostWithProductResponse {
    private ProductResponse product;

    public static ReviewPostWithProductResponse fromReviewPostWithProduct(ReviewPostWithProduct reviewPostWithProduct) {
        return ReviewPostWithProductResponse.builder()
                .product(ProductResponse.fromProduct(reviewPostWithProduct.getProduct()))
                .build();
    }
}
