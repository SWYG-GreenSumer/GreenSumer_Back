package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentCreateRequest {
    private String content;

    public static EventCommentCreateRequest of(String content){
        return new EventCommentCreateRequest(content);
    }
}
