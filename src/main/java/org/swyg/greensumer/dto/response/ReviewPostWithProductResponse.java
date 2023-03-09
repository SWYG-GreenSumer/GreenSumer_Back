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
    private Long id;
    private ReviewPostResponse reviewPost;
    private ProductResponse product;

    public static ReviewPostWithProductResponse fromReviewPostWithProduct(ReviewPostWithProduct reviewPostWithProduct) {
        return ReviewPostWithProductResponse.builder()
                .reviewPost(ReviewPostResponse.fromReviewPost(reviewPostWithProduct.getReviewPost()))
                .product(ProductResponse.fromProduct(reviewPostWithProduct.getProduct()))
                .build();
    }
}
