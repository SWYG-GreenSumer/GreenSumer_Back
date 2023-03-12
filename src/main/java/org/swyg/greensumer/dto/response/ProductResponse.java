package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.ReviewPostWithProduct;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String description;
    private Set<ImageResponse> images;

    public static ProductResponse fromProduct(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImages().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    public static ProductResponse fromReviewPostWithProduct(ReviewPostWithProduct product){
        return ProductResponse.builder()
                .id(product.getProduct().getId())
                .name(product.getProduct().getName())
                .price(product.getProduct().getPrice())
                .stock(product.getProduct().getStock())
                .description(product.getProduct().getDescription())
                .images(product.getProduct().getImages().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet()))
                .build();
    }

}
