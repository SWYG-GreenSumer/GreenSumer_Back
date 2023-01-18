package org.swyg.greensumer.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ProductEntityRepository;
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
import static org.swyg.greensumer.fixture.RequestFixture.getReviewPostCreateRequest;
import static org.swyg.greensumer.fixture.RequestFixture.getReviewPostModifyRequest;

@DisplayName("비즈니스로직 - 후기 게시글")
@ExtendWith({MockitoExtension.class})
class ReviewPostServiceTest {

    @InjectMocks private ReviewPostService sut;

    @Mock private UserEntityRepository userEntityRepository;
    @Mock private ProductEntityRepository productEntityRepository;
    @Mock private ReviewPostEntityRepository reviewPostEntityRepository;

    private static UserEntity userEntity;
    private static ProductEntity productEntity;
    private static ReviewPostEntity reviewPostEntity;

    @BeforeAll
    static void setUp() {
        userEntity = getUserEntity();
        productEntity = getProductEntity();
        reviewPostEntity = getReviewPostEntity();
    }

    @DisplayName("후기 게시글 생성 - 정상 요청")
    @Test
    void givenReviewPost_whenRequestingCreateReviewPost_thenReturnNothing() {
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(productEntityRepository.findById(any())).willReturn(Optional.of(productEntity));
        given(reviewPostEntityRepository.save(any())).willReturn(reviewPostEntity);

        // When
        sut.create(getReviewPostCreateRequest(), productEntity.getId(), userEntity.getUsername());

        //Then
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(1)).findById(any());
        verify(reviewPostEntityRepository, times(1)).save(any());
    }

    @DisplayName("후기 게시글 생성 - 로그인하지 않은 경우")
    @Test
    void givenReviewPost_whenRequestingCreateReviewPost_thenThrowInvalidPermissionException() {

        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.create(getReviewPostCreateRequest(), productEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(0)).findById(any());
        verify(reviewPostEntityRepository, times(0)).save(any());
    }

    @DisplayName("후기 게시글 생성 - 제품이 존재하지 않은 경우")
    @Test
    void givenReviewPost_whenRequestingCreateReviewPost_thenThrowProductNotFoundException() {
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(productEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.create(getReviewPostCreateRequest(), productEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.PRODUCT_NOT_FOUND);
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(1)).findById(any());
        verify(reviewPostEntityRepository, times(0)).save(any());
    }

    @DisplayName("후기 게시글 수정 - 정상 요청")
    @Test
    void givenReviewPost_whenRequestingModifyReviewPost_thenReturnNothing() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(getReviewPostEntity()));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(getUserEntity()));
        given(productEntityRepository.findById(any())).willReturn(Optional.of(productEntity));
        given(reviewPostEntityRepository.saveAndFlush(any())).willReturn(reviewPostEntity);

        // When
        ReviewPost modifiedReviewPost = sut.modify(getReviewPostModifyRequest(), productEntity.getId(), productEntity.getId(), userEntity.getUsername());

        //Then
        assertThat(modifiedReviewPost).isNotNull();
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(1)).findById(any());
        verify(reviewPostEntityRepository, times(1)).saveAndFlush(any());
    }

    @DisplayName("후기 게시글 수정 - 게시글이 존재하지 않은 경우")
    @Test
    void givenReviewPost_whenRequestingModifyReviewPost_thenThrowPostNotFoundException() {
        //when
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modify(getReviewPostModifyRequest(), productEntity.getId(), productEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(productEntityRepository, times(0)).getReferenceById(any());
        verify(reviewPostEntityRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("후기 게시글 수정 - 회원가입하지 않은 계정인 경우")
    @Test
    void givenReviewPost_whenRequestingModifyReviewPost_thenThrowUserNotFoundException() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(getReviewPostEntity()));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modify(getReviewPostModifyRequest(), productEntity.getId(), productEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(0)).findById(any());
        verify(reviewPostEntityRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("후기 게시글 수정 - 본인에 게시글이 아닌 경우")
    @Test
    void givenReviewPost_whenRequestingModifyReviewPost_thenThrowInvalidPermissionException() {
        //given
        String otherUsername = "other";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modify(getReviewPostModifyRequest(), reviewPostEntity.getId(), productEntity.getId(), otherUsername));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(productEntityRepository, times(0)).findById(any());
        verify(reviewPostEntityRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("후기 게시글 수정 - 제품이 존재하지 않은 경우")
    @Test
    void givenReviewPost_whenRequestingModifyReviewPost_thenThrowProductNotFoundException() {
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        given(productEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.modify(getReviewPostModifyRequest(), reviewPostEntity.getId(), productEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.PRODUCT_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(productEntityRepository, times(1)).findById(any());
        verify(reviewPostEntityRepository, times(0)).saveAndFlush(any());
    }

    @DisplayName("후기 게시글 삭제 - 정상 호출")
    @Test
    void givenPostId_whenRequestingDeleteReviewPost_thenReturnNothing() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
        willDoNothing().given(reviewPostEntityRepository).delete(any());

        // When
        sut.delete(reviewPostEntity.getId(), userEntity.getUsername());

        //Then
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewPostEntityRepository, times(1)).delete(any());
    }

    @DisplayName("후기 게시글 삭제 - 존재하지 않은 게시글인 경우")
    @Test
    void givenPostId_whenRequestingDeleteReviewPost_thenThrowPostNotFoundException() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.delete(reviewPostEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(0)).findByUsername(any());
        verify(reviewPostEntityRepository, times(0)).delete(any());
    }

    @DisplayName("후기 게시글 삭제 - 회원가입되지 않은 유저인 경우")
    @Test
    void givenPostId_whenRequestingDeleteReviewPost_thenThrowUserNotFoundException() {
        // Given
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.delete(reviewPostEntity.getId(), userEntity.getUsername()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewPostEntityRepository, times(0)).delete(any());
    }

    @DisplayName("후기 게시글 삭제 - 본인이 아닌 경우")
    @Test
    void givenPostId_whenRequestingDeleteReviewPost_thenThrowInvalidPermission() {
        // Given
        String otherUsername = "otherUsername";
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.of(reviewPostEntity));
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.delete(reviewPostEntity.getId(), otherUsername));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(reviewPostEntityRepository, times(1)).findById(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewPostEntityRepository, times(0)).delete(any());
    }

    @DisplayName("후기 게시글 리스트 조회 - 정상 호출")
    @Test
    void givenNothing_whenRequestingReviewPostList_thenReturnPageReviewPost() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        given(reviewPostEntityRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        sut.list(pageable);

        //Then
        verify(reviewPostEntityRepository, times(1)).findAll(pageable);
    }

    @DisplayName("본인이 작성한 후기 게시글 리스트 조회 - 정상 호출")
    @Test
    void givenUserInfo_whenRequestingHisReviewPostList_thenReturnPageReviewPost() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.ofNullable(userEntity));
        given(reviewPostEntityRepository.findAllByUser(userEntity, pageable)).willReturn(Page.empty());

        // When
        sut.mylist(userEntity.getUsername(), pageable);

        //Then
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewPostEntityRepository, times(1)).findAllByUser(userEntity, pageable);
    }

    @DisplayName("댓글 리스트를 가진 단일 게시글 조회 - 정상 호출")
    @Test
    void givenUserInfo_whenRequestingReviewPostWithCommentList_thenReturnReviewPostWithCommentList() {
        // Given
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.ofNullable(userEntity));
        given(reviewPostEntityRepository.findById(any())).willReturn(Optional.ofNullable(reviewPostEntity));

        // When
        sut.getPostAndComments(reviewPostEntity.getId(), userEntity.getUsername());

        //Then
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(reviewPostEntityRepository, times(1)).findById(any());
    }

}