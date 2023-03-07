package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.EventPostProductEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPostWithProduct {
    private Long id;
    private EventPost eventPost;
    private Product product;

    public static EventPostWithProduct fromEntity(EventPostProductEntity entity) {
        return EventPostWithProduct.builder()
                .id(entity.getId())
                .eventPost(EventPost.fromEntity(entity.getEvent()))
                .product(Product.fromEntity(entity.getProduct()))
                .build();
    }
}
