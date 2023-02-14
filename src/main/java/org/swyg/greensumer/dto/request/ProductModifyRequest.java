package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModifyRequest {
    private String name;
    private int price;
    private int stock;
    private String description;
    private List<Long> images;

    public static ProductModifyRequest of(String name, int price, int stock, String description, List<Long> images) {
        return new ProductModifyRequest(name, price, stock, description, images);
    }
}
