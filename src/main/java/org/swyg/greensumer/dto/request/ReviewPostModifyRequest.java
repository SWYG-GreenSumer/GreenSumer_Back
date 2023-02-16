package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostModifyRequest {
    private Long storeId;
    private String title;
    private String content;
    private String scope;
    private List<Long> products;
    private List<Long> images;

    public static ReviewPostModifyRequest of(Long storeId, String title, String content, String scope, List<Long> products, List<Long> images) {
        return new ReviewPostModifyRequest(storeId, title, content, scope, products, images);
    }
}
