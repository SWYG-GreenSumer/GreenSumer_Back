package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostCreateRequest {
    private Integer productId;
    private String title;
    private String content;
    private List<Integer> images;

    public static ReviewPostCreateRequest of(Integer productId, String title, String content, List<Integer> images) {
        return new ReviewPostCreateRequest(productId, title, content, images);
    }
}
