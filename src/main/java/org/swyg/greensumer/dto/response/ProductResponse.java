package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Product;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
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
}
