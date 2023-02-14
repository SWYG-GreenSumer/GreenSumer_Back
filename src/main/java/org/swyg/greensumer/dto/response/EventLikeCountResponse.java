package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.dto.EventPost;

@Getter
@Builder
public class EventLikeCountResponse {
    private Long postId;
    private int like;

    public static EventLikeCountResponse fromEventPost(EventPost eventPost){
        return EventLikeCountResponse.builder()
                .postId(eventPost.getId())
                .like(eventPost.getLikes())
                .build();
    }
}
