package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.dto.ReviewPostWithComment;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ImageEntityRepository;
import org.swyg.greensumer.repository.ProductEntityRepository;
import org.swyg.greensumer.repository.ReviewPostEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewPostService {

    private final UserEntityRepository userEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final ReviewPostEntityRepository reviewPostEntityRepository;
    private final ImageEntityRepository imageEntityRepository;

    public void create(ReviewPostCreateRequest request, Integer productId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has not permission", username)));

        ProductEntity productEntity = productEntityRepository.findById(productId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId)));

        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.save(ReviewPostEntity.of(
                productEntity,
                userEntity,
                request.getTitle(),
                request.getContent()
        ));

        if(request.getImages().size() != 0){
            List<ImageEntity> imageEntities = imageEntityRepository.findAllByIdIn(request.getImages());
            reviewPostEntity.addImages(imageEntities);
        }

    }

    @Transactional
    public ReviewPost modify(ReviewPostModifyRequest request, Integer postId, Integer productId, String username) {
        ReviewPostEntity reviewPost = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        if(!reviewPost.getUser().getUsername().equals(username)){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));


        ProductEntity productEntity = productEntityRepository.findById(productId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId)));

        reviewPost.setProduct(productEntity);
        reviewPost.setTitle(request.getTitle());
        reviewPost.setContent(request.getContent());

        if(request.getImages().size() != 0){
            List<ImageEntity> imageEntities = imageEntityRepository.findAllByIdIn(request.getImages());

            reviewPost.clearImages();
            reviewPost.addImages(imageEntities);
        }

        ReviewPostEntity updatedReviewPostEntity = reviewPostEntityRepository.saveAndFlush(reviewPost);

        return ReviewPost.fromEntity(updatedReviewPostEntity);
    }

    @Transactional
    public void delete(Integer postId, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        if(!reviewPostEntity.getUser().getUsername().equals(username)){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        reviewPostEntityRepository.delete(reviewPostEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> list(Pageable pageable) {
        return reviewPostEntityRepository.findAll(pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> mylist(String username, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        return reviewPostEntityRepository.findAllByUser(userEntity, pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public ReviewPostWithComment getPostAndComments(Integer postId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        return reviewPostEntityRepository.findById(postId)
                .map(ReviewPostWithComment::fromEntity)
                .orElseThrow(() -> { throw new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId));});
    }
}
