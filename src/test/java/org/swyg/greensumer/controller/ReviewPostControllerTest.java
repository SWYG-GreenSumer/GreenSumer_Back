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
import org.swyg.greensumer.service.ReviewCommentService;
import org.swyg.greensumer.service.ReviewPostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 후기 게시판")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class})
@WebMvcTest(ReviewPostController.class)
class ReviewPostControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private ReviewPostService reviewPostService;
    @MockBean private ReviewCommentService reviewCommentService;

    ReviewPostControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][GET] 후기 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPosts_thenReturnsReviewPosts() throws Exception {
        // Given
        willReturn(Page.empty()).given(reviewPostService).list(any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/reviews")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 후기 게시글 페이지 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPost_thenReturnsReviewPost() throws Exception {
        // Given
        willReturn(reviewPostWithComment()).given(reviewPostService).getPostAndComments(any(), anyString());

        // When & Then
        mvc.perform(get("/api/reviews/1")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 후기 게시글 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingReviewPostNotLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(reviewPostWithComment()).given(reviewPostService).getPostAndComments(any(), anyString());

        // When & Then
        mvc.perform(get("/api/reviews/1")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 본인이 작성한 후기 게시글 리스트 페이지 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPost_thenReturnsMyReviewPostList() throws Exception {
        // Given
        willReturn(Page.empty()).given(reviewPostService).myList(anyString(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/reviews/my")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 본인이 작성한 후기 게시글 리스트 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingMyReviewPostNotLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(Page.empty()).given(reviewPostService).myList(anyString(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/reviews/my")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 후기 게시글 생성 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePost_thenSaveReviewPost() throws Exception {
        // Given
        willDoNothing().given(reviewPostService).create(any(), anyString());

        // When & Then
        mvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 후기 게시글 생성 - 로그인하지 않은경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePostNotLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willDoNothing().given(reviewPostService).create(any(), anyString());

        // When & Then
        mvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenReturnReviewPost() throws Exception {
        // Given
        willReturn(reviewPost()).given(reviewPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPostNotLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(reviewPost()).given(reviewPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 본인이 작성한 글이 아닌 경우 에러발생")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingOtherModifyPost_thenThrowInvalid_Permission_Error() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(reviewPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenThrowNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(reviewPostService).modify(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewPostModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][Delete] 후기 게시글 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenDeleteReviewPost() throws Exception {
        // Given
        willDoNothing().given(reviewPostService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenThrowIsUnauthorizedException() throws Exception {
        // Given
        willDoNothing().given(reviewPostService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 본인이 작성한 글이 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPostFromOther_thenThrowIsUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(reviewPostService).delete(any(), any());

        // When & Then
        mvc.perform(delete("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 게시글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(reviewPostService).delete(any(), any());

        // When & Then
        mvc.perform(delete("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateReviewPostComment_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(reviewCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/reviews/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenPostIdAndComment_whenRequestCreateReviewPostCommentNotLogin_thenThrowInUnAuthorized() throws Exception {
        // Given
        willDoNothing().given(reviewCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/reviews/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 게시글이 존재하지 않은 경우")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(reviewCommentService).createComment(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/reviews/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostComment_thenReturnNothing() throws Exception {
        // Given
        willReturn(reviewComment()).given(reviewCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willReturn(reviewComment()).given(reviewCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(reviewCommentService).modifyComment(any(), any(), any(),anyString());

        // When & Then
        mvc.perform(put("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(reviewCommentService).modifyComment(any(), any(), any(), any());

        // When & Then
        mvc.perform(put("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ReviewCommentModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostComment_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(reviewCommentService).deleteComment(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willDoNothing().given(reviewCommentService).deleteComment(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(reviewCommentService).deleteComment(any(), any(), anyString());

        mvc.perform(delete("/api/reviews/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).given(reviewCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/reviews/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 댓글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteComment_thenThrowCommentNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND)).given(reviewCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/reviews/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

}