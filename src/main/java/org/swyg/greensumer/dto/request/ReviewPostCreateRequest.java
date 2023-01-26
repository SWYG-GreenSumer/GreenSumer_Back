package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostCreateRequest {
    private Long storeId;
    private Long productId;
    private String title;
    private String content;
    private List<Long> images;

    public static ReviewPostCreateRequest of(Long storeId, Long productId, String title, String content, List<Long> images) {
        return new ReviewPostCreateRequest(storeId, productId, title, content, images);
    }
}
