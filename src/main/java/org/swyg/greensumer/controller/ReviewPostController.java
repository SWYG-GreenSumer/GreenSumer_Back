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

    @PostMapping
    public Response<Void> create(@RequestBody ReviewPostCreateRequest request, Authentication authentication) {
        reviewPostService.create(request, authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<ReviewPostResponse> modify(@PathVariable Long postId, @RequestBody ReviewPostModifyRequest request, Authentication authentication) {
        ReviewPost reviewPost = reviewPostService.modify(request, postId, authentication.getName());
        return Response.success(ReviewPostResponse.fromReviewPost(reviewPost));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Long postId, Authentication authentication) {
        reviewPostService.delete(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping
    public Response<Page<ReviewPostsResponse>> list(Pageable pageable) {
        return Response.success(reviewPostService.list(pageable).map(ReviewPostsResponse::fromReviewPost));
    }

    @GetMapping("/news")
    public Response<Page<ReviewNewPostResponse>> newList(Pageable pageable) {
        return Response.success(reviewPostService.list(pageable).map(ReviewNewPostResponse::fromReviewPost));
    }

    @GetMapping("/{postId}")
    public Response<ReviewPostWithCommentResponse> getPostAndComments(@PathVariable Long postId, Authentication authentication) {
        ReviewPostWithComment postWithComment = reviewPostService.getPostAndComments(postId, authentication.getName());
        return Response.success(ReviewPostWithCommentResponse.fromReviewPostWithComment(postWithComment));
    }

    @GetMapping("/my")
    public Response<Page<ReviewPostResponse>> mylist(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.myList(authentication.getName(), pageable).map(ReviewPostResponse::fromReviewPost));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> createComment(@PathVariable Long postId, @RequestBody ReviewCommentCreateRequest request, Authentication authentication) {
        reviewCommentService.createComment(postId, request.getContent(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Response<ReviewCommentResponse> modifyComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody ReviewCommentModifyRequest request,
            Authentication authentication
    ) {
        ReviewComment reviewComment = reviewCommentService.modifyComment(postId, commentId, request.getContent(), authentication.getName());
        return Response.success(ReviewCommentResponse.fromReviewComment(reviewComment));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public Response<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        reviewCommentService.deleteComment(postId, commentId, authentication.getName());
        return Response.success();
    }

    @PostMapping("/{postId}/like")
    public Response<ReviewLikeCountResponse> likeReviewPost(@PathVariable Long postId, Authentication authentication) {
        ReviewPost reviewPost = reviewPostService.likeReviewPost(postId, authentication.getName());
        return Response.success(ReviewLikeCountResponse.fromReviewPost(reviewPost));
    }
}
