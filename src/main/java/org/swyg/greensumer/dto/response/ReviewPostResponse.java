package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.ReviewPost;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostResponse {
    private Long id;
    private UserResponse user;
    private Set<ProductResponse> products;
    private Set<ImageResponse> images;
    private String title;
    private String content;
    private String scope;
    private Long views;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewPostResponse fromReviewPost(ReviewPost reviewPost) {
        return new ReviewPostResponse(
                reviewPost.getId(),
                UserResponse.fromUser(reviewPost.getUser()),
                reviewPost.getProducts().stream()
                        .map(ProductResponse::fromProduct)
                        .collect(Collectors.toUnmodifiableSet()),
                reviewPost.getImages().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet()),
                reviewPost.getTitle(),
                reviewPost.getContent(),
                reviewPost.getScope(),
                reviewPost.getViews(),
                reviewPost.getRegisteredAt(),
                reviewPost.getUpdatedAt(),
                reviewPost.getDeletedAt()
        );
    }
}
