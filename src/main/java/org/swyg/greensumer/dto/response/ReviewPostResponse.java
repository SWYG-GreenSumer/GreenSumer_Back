package org.swyg.greensumer.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.dto.ReviewPost;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewPostResponse {
    private Long id;
    private String writer;
    private Set<ReviewPostWithProductResponse> products;
    private Set<ImageResponse> images;
    private String title;
    private String content;
    private String scope;
    private Long views;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    public static ReviewPostResponse fromReviewPost(ReviewPost reviewPost) {
        return ReviewPostResponse.builder()
                .id(reviewPost.getId())
                .writer(reviewPost.getUser().getUsername())
                .title(reviewPost.getTitle())
                .content(reviewPost.getContent())
                .views(reviewPost.getViews())
                .scope(reviewPost.getScope())
                .products(reviewPost.getProducts().stream()
                        .map(ReviewPostWithProductResponse::fromReviewPostWithProduct)
                        .collect(Collectors.toUnmodifiableSet()))
                .images(reviewPost.getImages().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet()))
                .registeredAt(reviewPost.getRegisteredAt())
                .updatedAt(reviewPost.getUpdatedAt())
                .deletedAt(reviewPost.getDeletedAt())
                .build();
    }
}
