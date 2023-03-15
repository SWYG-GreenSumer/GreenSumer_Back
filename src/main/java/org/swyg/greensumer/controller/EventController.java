package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.EventComment;
import org.swyg.greensumer.dto.EventPost;
import org.swyg.greensumer.dto.EventPostWithComment;
import org.swyg.greensumer.dto.request.EventCommentCreateRequest;
import org.swyg.greensumer.dto.request.EventCommentModifyRequest;
import org.swyg.greensumer.dto.request.EventPostCreateRequest;
import org.swyg.greensumer.dto.request.EventPostModifyRequest;
import org.swyg.greensumer.dto.response.*;
import org.swyg.greensumer.service.EventCommentService;
import org.swyg.greensumer.service.EventPostService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventPostService eventPostService;
    private final EventCommentService eventCommentService;

    @PostMapping
    public Response<Void> createEvent(@RequestBody EventPostCreateRequest request, Authentication authentication){
        eventPostService.create(request, authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<EventPostResponse> modifyEvent(@PathVariable Long postId, @RequestBody EventPostModifyRequest request, Authentication authentication) {
        EventPost eventPost = eventPostService.modify(request, postId, authentication.getName());
        return Response.success(EventPostResponse.fromEventPost(eventPost));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> deleteEvent(@PathVariable Long postId, Authentication authentication) {
        eventPostService.delete(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping
    public Response<Page<EventPostResponse>> eventList(Pageable pageable, Authentication authentication) {
        return Response.success(eventPostService.list(pageable).map(EventPostResponse::fromEventPost));
    }

    @GetMapping("/{postId}")
    public Response<EventPostWithCommentResponse> getEventPostAndComments(@PathVariable Long postId, Authentication authentication) {
        EventPostWithComment eventPostWithComment = eventPostService.getPostAndComments(postId, authentication.getName());
        return Response.success(EventPostWithCommentResponse.fromEventPostWithComment(eventPostWithComment));
    }

    @GetMapping("/my")
    public Response<Page<EventPostResponse>> myEventList(Pageable pageable, Authentication authentication) {
        return Response.success(eventPostService.myList(authentication.getName(), pageable).map(EventPostResponse::fromEventPost));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> createComment(@PathVariable Long postId, @RequestBody EventCommentCreateRequest request, Authentication authentication) {
        eventCommentService.createComment(postId, request.getContent(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Response<EventCommentResponse> modifyComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody EventCommentModifyRequest request,
            Authentication authentication
    ) {
        EventComment reviewComment = eventCommentService.modifyComment(postId, commentId, request.getContent(), authentication.getName());
        return Response.success(EventCommentResponse.fromEventComment(reviewComment));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public Response<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        eventCommentService.deleteComment(postId, commentId, authentication.getName());
        return Response.success();
    }

    @PostMapping("/{postId}/like")
    public Response<EventLikeCountResponse> likeEventPost(@PathVariable Long postId, Authentication authentication) {
        EventPost eventPost = eventPostService.likeEventPost(postId, authentication.getName());
        return Response.success(EventLikeCountResponse.fromEventPost(eventPost));
    }
}
