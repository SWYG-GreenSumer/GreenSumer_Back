package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.EventCommentService;
import org.swyg.greensumer.service.EventPostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 이벤트 게시판")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class})
@WebMvcTest(EventController.class)
class EventControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private EventPostService eventPostService;
    @MockBean private EventCommentService eventCommentService;

    public EventControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][GET] 이벤트 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingEventPosts_thenReturnsEventPosts() throws Exception {
        // Given
        willReturn(Page.empty()).given(eventPostService).list(any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/events")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 이벤트 게시글 페이지 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingEventPost_thenReturnsEventPost() throws Exception {
        // Given
        willReturn(eventPostWithComment()).given(eventPostService).getPostAndComments(any(), anyString());

        // When & Then
        mvc.perform(get("/api/events/1")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 이벤트 게시글 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingEventPost_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(eventPostWithComment()).given(eventPostService).getPostAndComments(any(), anyString());

        // When & Then
        mvc.perform(get("/api/events/1")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 본인이 작성한 이벤트 게시글 리스트 페이지 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPost_thenReturnsMyReviewPostList() throws Exception {
        // Given
        willReturn(Page.empty()).given(eventPostService).myList(anyString(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/events/my")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 본인이 작성한 이벤트 게시글 리스트 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingMyReviewPostNotLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(Page.empty()).given(eventPostService).myList(anyString(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/events/my")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 이벤트 게시글 생성 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePost_thenSaveReviewPost() throws Exception {
        // Given
        willDoNothing().given(eventPostService).create(any(), anyString());

        // When & Then
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 이벤트 게시글 생성 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePost_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willDoNothing().given(eventPostService).create(any(), anyString());

        // When & Then
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 이벤트 게시글 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenSaveReviewPost() throws Exception {
        // Given
        willReturn(eventPost()).given(eventPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 이벤트 게시글 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(eventPost()).given(eventPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 이벤트 게시글 수정 - 본인이 작성한 글이 아닌 경우 에러발생")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingOtherModifyPost_thenThrowInvalid_Permission_Error() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(eventPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 이벤트 게시글 수정 - 글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenThrowNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(eventPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][Delete] 후기 게시글 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteEventPost_thenDeleteReviewPost() throws Exception {
        // Given
        willDoNothing().given(eventPostService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteEventPost_thenThrowIsUnauthorizedException() throws Exception {
        // Given
        willDoNothing().given(eventPostService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 본인이 작성한 글이 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteEventPostFromOther_thenThrowIsUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(eventPostService).delete(any(), any());

        // When & Then
        mvc.perform(delete("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 게시글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteEventPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(eventPostService).delete(any(), any());

        // When & Then
        mvc.perform(delete("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateEventPostComment_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(eventCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/events/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenPostIdAndComment_whenRequestCreateEventPostCommentNotLogin_thenThrowInUnAuthorized() throws Exception {
        // Given
        willDoNothing().given(eventCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/events/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 게시글이 존재하지 않은 경우")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(eventCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/events/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyEventPostComment_thenReturnNothing() throws Exception {
        // Given
        willReturn(eventComment()).given(eventCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyEventPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(eventComment()).given(eventCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyEventPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(eventCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(eventCommentService).modifyComment(any(), any(), any(), any());

        // When & Then
        mvc.perform(put("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(EventCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteEventPostComment_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(eventCommentService).deleteComment(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteEventPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willDoNothing().given(eventCommentService).deleteComment(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteEventPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(eventCommentService).deleteComment(any(), any(), anyString());

        mvc.perform(delete("/api/events/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(eventCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/events/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 댓글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteComment_thenThrowCommentNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND)).given(eventCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/events/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

}