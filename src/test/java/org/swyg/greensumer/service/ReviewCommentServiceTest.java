package org.swyg.greensumer.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyg.greensumer.domain.ReviewCommentEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewComment;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ReviewCommentRepository;
import org.swyg.greensumer.repository.ReviewPostEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.swyg.greensumer.fixture.Fixtures.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ReviewCommentServiceTest {

    @InjectMocks private ReviewCommentService sut;

    @Mock private ReviewPostEntityRepository reviewPostEntityRepository;
    @Mock private UserEntityRepository userEntityRepository;
    @Mock private ReviewCommentRepository reviewCommentRepository;

    private static ReviewPostEntity reviewPostEntity;
    private static ReviewCommentEntity reviewCommentEntity;
    private static UserEntity userEntity;

    @BeforeAll
    static void setUp(){
        reviewPostEntity = getReviewPostEntity();
        reviewCommentEntity = getReviewCommentEntity();
        userEntity = getUserEntity();
    }

    @DisplayName("댓글 작성 - 정상 호출")
    @Test
    void givenPostIdAndContent_whenRequestingCreateComment_thenReturnNothing() {
        // Given
        String content = "content";

        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(reviewCommentRepository.save(any())).willReturn(reviewCommentEntity);

        // When
        sut.createComment(reviewPostEntity.getId(), content, userEntity.getUsername());

        //Then
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(1)).save(any());
    }

    @DisplayName("댓글 작성 - 존재하지 않는 게시글인 경우")
    @Test
    void givenPostIdAndContent_whenRequestingCreateComment_thenThrowPostNOTFoundException() {
        // Given
        String content = "content";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.createComment(reviewPostEntity.getId(), content, userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).save(any());
    }

    @DisplayName("댓글 작성 - 회원가입하지 않은 유저인경우")
    @Test
    void givenPostIdAndContent_whenRequestingCreateComment_thenThrowUserNotFoundException() {
        // Given
        String content = "content";

        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.createComment(reviewPostEntity.getId(), content, userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).save(any());
    }

    @DisplayName("댓글 수정 - 정상 호출")
    @Test
    void givenPostIdAndContent_whenRequestingModifyComment_thenReturnNothing() {
        // Given
        String content = "modified content";

        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(reviewCommentRepository.findById(any())).willReturn(Optional.of(reviewCommentEntity));
        given(reviewCommentRepository.saveAndFlush(any())).willReturn(reviewCommentEntity);

        // When
        ReviewComment reviewComment = sut.modifyComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), content, userEntity.getUsername());

        //Then
        assertThat(reviewComment).isNotNull();
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(1)).findById(any());
        verify(reviewCommentRepository, times(1)).saveAndFlush(any());
    }

    @DisplayName("댓글 수정 - 존재하지 않는 게시글인 경우")
    @Test
    void givenPostIdAndContent_whenRequestingModifyComment_thenThrowPostNOTFoundException() {
        // Given
        String content = "modified content";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modifyComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), content, userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).findById(any());
        verify(reviewCommentRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("댓글 수정 - 회원가입하지 않은 유저인경우")
    @Test
    void givenPostIdAndContent_whenRequestingModifyComment_thenThrowUserNotFoundException() {
        // Given
        String content = "modified content";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modifyComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), content, userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).findById(any());
        verify(reviewCommentRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("댓글 수정 - 본인에 댓글이 아닌 경우")
    @Test
    void givenPostIdAndContent_whenRequestingModifyComment_thenThrowInvalidPermissionException() {
        // Given
        String other = "otherUsername";
        String content = "modified content";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(reviewCommentRepository.findById(any())).willReturn(Optional.of(reviewCommentEntity));

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modifyComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), content, other));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(1)).findById(any());
        verify(reviewCommentRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("댓글 삭제 - 정상 호출")
    @Test
    void givenPostId_whenRequestingDeleteComment_thenReturnNothing() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(reviewCommentRepository.findById(any())).willReturn(Optional.of(reviewCommentEntity));
        willDoNothing().given(reviewCommentRepository).deleteById(any());

        // When
        sut.deleteComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), userEntity.getUsername());

        //Then
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(1)).findById(any());
        verify(reviewCommentRepository, times(1)).deleteById(any());
    }

    @DisplayName("댓글 삭제 - 존재하지 않는 게시글인 경우")
    @Test
    void givenPostId_whenRequestingDeleteComment_thenThrowPostNOTFoundException() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.deleteComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).findById(any());
        verify(reviewCommentRepository, times(0)).deleteById(any());
    }

    @DisplayName("댓글 삭제 - 회원가입하지 않은 유저인경우")
    @Test
    void givenPostId_whenRequestingDeleteComment_thenThrowUserNotFoundException() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.deleteComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(0)).findById(any());
        verify(reviewCommentRepository, times(0)).deleteById(any());
    }

    @DisplayName("댓글 삭제 - 본인에 댓글이 아닌 경우")
    @Test
    void givenPostId_whenRequestingDeleteComment_thenThrowInvalidPermissionException() {
        // Given
        String other = "other";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(reviewCommentRepository.findById(any())).willReturn(Optional.of(reviewCommentEntity));

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.deleteComment(reviewPostEntity.getId(), reviewCommentEntity.getId(), other));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewCommentRepository, times(1)).findById(any());
        verify(reviewCommentRepository, times(0)).deleteById(any());
    }

}
