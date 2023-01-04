package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.ReviewPostResponse;
import org.swyg.greensumer.service.ReviewPostService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;

    @PostMapping
    public Response<Void> create(@RequestBody ReviewPostCreateRequest request, Authentication authentication) {
        reviewPostService.create(request, request.getProductId(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<ReviewPostResponse> modify(@PathVariable Integer postId, @RequestBody ReviewPostModifyRequest request, Authentication authentication) {
        ReviewPost reviewPost = reviewPostService.modify(request, postId, request.getProductId(), authentication.getName());

        return Response.success(ReviewPostResponse.fromReviewPost(reviewPost));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        reviewPostService.delete(postId, authentication.getName());

        return Response.success();
    }

    // TODO: occur INTERNAL SERVER ERROR
    @GetMapping
    public Response<Page<ReviewPostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.list(pageable).map(ReviewPostResponse::fromReviewPost));
    }

    // TODO: occur INTERNAL SERVER ERROR
    @GetMapping("/{postId}")
    public Response<ReviewPostResponse> getPost(@PathVariable Integer postId, Authentication authentication) {
        ReviewPost post = reviewPostService.getPost(postId, authentication.getName());
        return Response.success(ReviewPostResponse.fromReviewPost(post));
    }

    @GetMapping("/user")
    public Response<Page<ReviewPostResponse>> mylist(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.mylist(pageable, authentication.getName()).map(ReviewPostResponse::fromReviewPost));
    }

}
