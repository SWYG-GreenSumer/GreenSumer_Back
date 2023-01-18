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
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.ReviewCommentResponse;
import org.swyg.greensumer.dto.response.ReviewPostResponse;
import org.swyg.greensumer.dto.response.ReviewPostWithCommentResponse;
import org.swyg.greensumer.service.ReviewCommentService;
import org.swyg.greensumer.service.ReviewPostService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;
    private final ReviewCommentService reviewCommentService;

    @PostMapping
    public Response<Void> create(@RequestBody ReviewPostCreateRequest request, Authentication authentication) {
        reviewPostService.create(request, request.getProductId(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<ReviewPostResponse> modify(@PathVariable Integer postId, @RequestBody ReviewPostModifyRequest request, Authentication authentication) {
        System.out.println("TEST ***********");
        System.out.println("REQUEST : " + request.toString());

        ReviewPost reviewPost = reviewPostService.modify(request, postId, request.getProductId(), authentication.getName());


        return Response.success(ReviewPostResponse.fromReviewPost(reviewPost));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        reviewPostService.delete(postId, authentication.getName());

        return Response.success();
    }

    @GetMapping
    public Response<Page<ReviewPostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.list(pageable).map(ReviewPostResponse::fromReviewPost));
    }

    @GetMapping("/{postId}")
    public Response<ReviewPostWithCommentResponse> getPostAndComments(@PathVariable Integer postId, Authentication authentication) {
        ReviewPostWithComment postWithComment = reviewPostService.getPostAndComments(postId, authentication.getName());
        return Response.success(ReviewPostWithCommentResponse.fromReviewPostWithComment(postWithComment));
    }

    @GetMapping("/my")
    public Response<Page<ReviewPostResponse>> mylist(Pageable pageable, Authentication authentication) {
        return Response.success(reviewPostService.mylist(authentication.getName(), pageable).map(ReviewPostResponse::fromReviewPost));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> createComment(@PathVariable Integer postId, @RequestBody ReviewCommentCreateRequest request, Authentication authentication) {
        reviewCommentService.createComment(postId, request.getContent(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Response<ReviewCommentResponse> modifyComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestBody ReviewCommentModifyRequest request,
            Authentication authentication
    ) {
        ReviewComment reviewComment = reviewCommentService.modifyComment(postId, commentId, request.getContent(), authentication.getName());
        return Response.success(ReviewCommentResponse.fromReviewComment(reviewComment));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public Response<Void> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, Authentication authentication) {
        reviewCommentService.deleteComment(postId, commentId, authentication.getName());
        return Response.success();
    }
}
