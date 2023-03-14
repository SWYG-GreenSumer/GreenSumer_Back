package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.ReviewComment;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.dto.ReviewPostWithComment;
import org.swyg.greensumer.dto.request.ReviewCommentCreateRequest;
import org.swyg.greensumer.dto.request.ReviewCommentModifyRequest;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.dto.response.*;
import org.swyg.greensumer.service.ReviewCommentService;
import org.swyg.greensumer.service.ReviewPostService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;
    private final ReviewCommentService reviewCommentService;

    @GetMapping("/test")
    public Response<Void> test() {
        return Response.success();
    }

    @PostMapping
    public Response<Void> create(@RequestBody ReviewPostCreateRequest request, Authentication authentication) {
        reviewPostService.create(request, authentication.getName());
        return Response.success();
    }

    @PutMapping("/{reviewId}")
    public Response<ReviewPostResponse> modify(@PathVariable Long reviewId, @RequestBody ReviewPostModifyRequest request, Authentication authentication) {
        ReviewPost reviewPost = reviewPostService.modify(request, reviewId, authentication.getName());
        return Response.success(ReviewPostResponse.fromReviewPost(reviewPost));
    }

    @DeleteMapping("/{reviewId}")
    public Response<Void> delete(@PathVariable Long reviewId, Authentication authentication) {
        reviewPostService.delete(reviewId, authentication.getName());
        return Response.success();
    }

    @GetMapping
    public Response<Page<ReviewPostResponse>> list(Pageable pageable) {
        return Response.success(reviewPostService.list(pageable).map(ReviewPostResponse::fromReviewPost));
    }

    @GetMapping("/news")
    public Response<Page<ReviewNewPostResponse>> newList(Pageable pageable) {
        return Response.success(reviewPostService.list(pageable).map(ReviewNewPostResponse::fromReviewPost));
    }

    @GetMapping("/{reviewId}")
    public Response<ReviewPostWithCommentResponse> getPostAndComments(@PathVariable Long reviewId, Authentication authentication) {
        ReviewPostWithComment postWithComment = reviewPostService.getPostAndComments(reviewId, authentication.getName());
        return Response.success(ReviewPostWithCommentResponse.fromReviewPostWithComment(postWithComment));
    }

    @GetMapping("/my")
    public Response<Page<ReviewPostResponse>> mylist(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.myList(authentication.getName(), pageable).map(ReviewPostResponse::fromReviewPost));
    }

    @PostMapping("/{reviewId}/comments")
    public Response<Void> createComment(@PathVariable Long reviewId, @RequestBody ReviewCommentCreateRequest request, Authentication authentication) {
        reviewCommentService.createComment(reviewId, request.getContent(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{reviewId}/comments/{commentId}")
    public Response<ReviewCommentResponse> modifyComment(
            @PathVariable Long reviewId,
            @PathVariable Long commentId,
            @RequestBody ReviewCommentModifyRequest request,
            Authentication authentication
    ) {
        ReviewComment reviewComment = reviewCommentService.modifyComment(reviewId, commentId, request.getContent(), authentication.getName());
        return Response.success(ReviewCommentResponse.fromReviewComment(reviewComment));
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public Response<Void> deleteComment(@PathVariable Long reviewId, @PathVariable Long commentId, Authentication authentication) {
        reviewCommentService.deleteComment(reviewId, commentId, authentication.getName());
        return Response.success();
    }

    @PostMapping("/{reviewId}/like")
    public Response<ReviewLikeCountResponse> likeReviewPost(@PathVariable Long reviewId, Authentication authentication) {
        ReviewPost reviewPost = reviewPostService.likeReviewPost(reviewId, authentication.getName());
        return Response.success(ReviewLikeCountResponse.fromReviewPost(reviewPost));
    }
}
