package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ReviewPostProductEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPostWithProduct {
    private Long id;
    private ReviewPost reviewPost;
    private Product product;

    public static ReviewPostWithProduct getProductFromEntity(ReviewPostProductEntity entity) {
        return ReviewPostWithProduct.builder()
                .product(Product.fromEntity(entity.getProduct()))
                .build();
    }
}
