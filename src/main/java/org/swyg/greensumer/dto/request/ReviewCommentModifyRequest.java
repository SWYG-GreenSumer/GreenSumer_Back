package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentModifyRequest {
    private String content;

    public static ReviewCommentModifyRequest of(String content){
        return new ReviewCommentModifyRequest(content);
    }
}
