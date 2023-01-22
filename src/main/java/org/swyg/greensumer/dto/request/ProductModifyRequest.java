package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductModifyRequest {
    private String name;
    private int price;
    private int stock;
    private String description;
    private List<Integer> images;

    public static ProductModifyRequest of(String name, int price, int stock, String description, List<Integer> images) {
        return new ProductModifyRequest(name, price, stock, description, images);
    }
}
