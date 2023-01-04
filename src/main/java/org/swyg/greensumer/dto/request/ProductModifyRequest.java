package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModifyRequest {
    private String name;
    private int price;
    private int stock;
    private String description;
    private String image;

    public static ProductModifyRequest of(String name, int price, int stock, String description, String image) {
        return new ProductModifyRequest(name, price, stock, description, image);
    }
}
