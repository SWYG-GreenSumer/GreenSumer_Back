package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.ReviewCommentService;
import org.swyg.greensumer.service.ReviewPostService;
import org.swyg.greensumer.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 후기 게시판")
@ActiveProfiles("test")
@WebMvcTest(ReviewPostController.class)
class ReviewPostControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private ReviewPostService reviewPostService;
    @MockBean private ReviewCommentService reviewCommentService;
    @MockBean private UserService userService;


    ReviewPostControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][GET] 후기 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingReviewPosts_thenReturnsReviewPosts() throws Exception {
        // Given
        given(reviewPostService.list(any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // When & Then
        then(reviewPostService).should().list(any(Pageable.class));
    }

    @DisplayName("[view][GET] 후기 게시글 페이지 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPost_thenReturnsReviewPost() throws Exception {
        // Given
        Integer postId = 1;
        given(reviewPostService.getPostAndComments(eq(postId), any())).willReturn(getReviewPostWithComment());

        mvc.perform(get("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // When & Then
        then(reviewPostService).should().getPostAndComments(eq(postId), any());
    }

    @DisplayName("[view][GET] 후기 게시글 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingReviewPostNotLogin_thenThrowUnauthroizedException() throws Exception {
        // Given
        Integer postId = 1;
        mvc.perform(get("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // When & Then
        then(reviewPostService).shouldHaveNoInteractions();
    }

    @DisplayName("[view][GET] 본인이 작성한 후기 게시글 리스트 페이지 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingReviewPost_thenReturnsMyReviewPostList() throws Exception {
        //when
        when(reviewPostService.mylist(any(), any())).thenReturn(Page.empty());

        mvc.perform(get("/api/v1/posts/my")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        then(reviewPostService).should().mylist(any(), any());
    }

    @DisplayName("[view][GET] 본인이 작성한 후기 게시글 리스트 페이지 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingMyReviewPostNotLogin_thenThrowUnauthroizedException() throws Exception {
        mvc.perform(get("/api/v1/posts/my")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());

        then(reviewPostService).shouldHaveNoInteractions();
    }

    @DisplayName("[view][POST] 후기 게시글 생성 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePost_thenSaveReviewPost() throws Exception {
        mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostCreateRequest()))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 후기 게시글 생성 - 로그인하지 않은경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingCreatePostNotLogin_thenThrowUnauthroizedException() throws Exception {
        mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostCreateRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenReturnReviewPost() throws Exception {
        when(reviewPostService.modify(any(), any(), any(), any())).thenReturn(getReviewPost());

        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPostNotLogin_thenThrowUnauthroizedException() throws Exception {
        when(reviewPostService.modify(any(), any(), any(), any())).thenReturn(getReviewPost());

        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 본인이 작성한 글이 아닌 경우 에러발생")
    @WithAnonymousUser
    @Test
    void givenReviewPostInfo_whenRequestingOtherModifyPost_thenThrowInvalid_Permission_Error() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(reviewPostService).modify(any(), any(), any(), any());

        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 후기 게시글 수정 - 글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostInfo_whenRequestingModifyPost_thenThrowNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).when(reviewPostService).modify(any(), any(), any(), any());

        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][Delete] 후기 게시글 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenDeleteReviewPost() throws Exception {
        mvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenThrowIsUnauthorizedException() throws Exception {
        mvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 본인이 작성한 글이 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPostFromOther_thenThrowIsUnauthorizedException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(reviewPostService).delete(any(), any());

        mvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 후기 게시글 삭제 - 게시글이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenReviewPostId_whenRequestDeleteReviewPost_thenThrowPostNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).when(reviewPostService).delete(any(), any());

        mvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateReviewPostComment_thenReturnNothing() throws Exception {
        mvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentCreateRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenPostIdAndComment_whenRequestCreateReviewPostCommentNotLogin_thenThrowInUnAuthorized() throws Exception {
        mvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentCreateRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 댓글 작성 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenPostIdAndComment_whenRequestCreateCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).when(reviewCommentService).createComment(any(), any(), any());

        mvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentCreateRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostComment_thenReturnNothing() throws Exception {
        when(reviewCommentService.modifyComment(any(), any(), any(), any())).thenReturn(getReviewComment());

        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentModifyRequest()))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentModifyRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyReviewPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentModifyRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 댓글 수정 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendIdAndComment_whenRequestModifyCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).when(reviewCommentService).modifyComment(any(), any(), any(), any());

        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewCommentModifyRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 정상호출")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostComment_thenReturnNothing() throws Exception {
        mvc.perform(delete("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostCommentNoLogin_thenThrowUnAuthorizedException() throws Exception {
        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 작성자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenCommendId_whenRequestDeleteReviewPostCommentFromOther_thenThrowUnAuthorizedException() throws Exception {
        mvc.perform(put("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 게시글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteCommentAtNotExistPost_thenThrowPostNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND)).when(reviewCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/v1/posts/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 댓글 삭제 요청 - 댓글이 존해하지 않은 경우")
    @WithMockUser
    @Test
    void givenCommendId_whenRequestDeleteComment_thenThrowCommentNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND)).when(reviewCommentService).deleteComment(any(), any(), any());

        mvc.perform(put("/api/v1/posts/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

}