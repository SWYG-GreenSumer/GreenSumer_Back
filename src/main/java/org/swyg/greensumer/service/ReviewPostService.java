package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ProductEntityRepository;
import org.swyg.greensumer.repository.ReviewPostEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

@RequiredArgsConstructor
@Service
public class ReviewPostService {

    private final UserEntityRepository userEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final ReviewPostEntityRepository reviewPostEntityRepository;

    @Transactional
    public void create(ReviewPostCreateRequest request, Integer productId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        ProductEntity productEntity = productEntityRepository.findById(productId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId)));

        reviewPostEntityRepository.save(ReviewPostEntity.of(
                productEntity,
                userEntity,
                request.getTitle(),
                request.getContent(),
                request.getHashtag(),
                request.getImagePath()
        ));
    }

    public ReviewPost modify(ReviewPostModifyRequest request, Integer postId, Integer productId, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        ProductEntity productEntity = productEntityRepository.findById(productId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId)));

        if (reviewPostEntity.getUser() != userEntity) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        reviewPostEntity.setProductEntity(productEntity);
        reviewPostEntity.setTitle(request.getTitle());
        reviewPostEntity.setContent(request.getContent());
        reviewPostEntity.setHashtag(request.getHashtag());
        reviewPostEntity.setImagePath(request.getImagePath());

        ReviewPostEntity updatedReviewPostEntity = reviewPostEntityRepository.saveAndFlush(reviewPostEntity);

        return ReviewPost.fromEntity(updatedReviewPostEntity);
    }

    @Transactional
    public void delete(Integer postId, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        if(reviewPostEntity.getUser() != userEntity){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        // TODO: 댓글을 함께 삭제해주어야한다.
        reviewPostEntityRepository.delete(reviewPostEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> list(Pageable pageable) {
        return reviewPostEntityRepository.findAll(pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> mylist(Pageable pageable, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        return reviewPostEntityRepository.findAllByUser(userEntity, pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public ReviewPost getPost(Integer postId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId));
        });

        return ReviewPost.fromEntity(reviewPostEntity);
    }
}
