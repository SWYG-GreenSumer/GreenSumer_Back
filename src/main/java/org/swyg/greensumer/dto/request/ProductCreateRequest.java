package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    private String name;
    private int price;
    private int stock;
    private String description;
    private List<Integer> images;

    public static ProductCreateRequest of(String name, int price, int stock, String description, List<Integer> images) {
        return new ProductCreateRequest(name, price, stock, description, images);
    }
}
