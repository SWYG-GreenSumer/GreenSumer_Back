package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentModifyRequest {
    private String content;

    public static EventCommentModifyRequest of(String content){
        return new EventCommentModifyRequest(content);
    }
}
