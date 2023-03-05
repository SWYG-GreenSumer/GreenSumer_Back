package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.ReviewPost;
import org.swyg.greensumer.dto.ReviewPostWithComment;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.review.ReviewPostEntityRepository;
import org.swyg.greensumer.repository.review.ReviewPostLikeEntityRepository;
import org.swyg.greensumer.repository.review.ViewsCacheRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.swyg.greensumer.service.ImageService.IMAGE_UPLOAD_MAX_COUNT;

@RequiredArgsConstructor
@Service
public class ReviewPostService {

    private final ReviewPostEntityRepository reviewPostEntityRepository;
    private final UserEntityRepositoryService userEntityRepositoryService;
    private final StoreService storeService;
    private final ImageService imageService;
    private final ReviewPostLikeEntityRepository reviewPostLikeEntityRepository;
    private final ViewsCacheRepository viewsCacheRepository;

    @Transactional
    public void create(ReviewPostCreateRequest request, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        List<ProductEntity> productEntities = storeService.getProductListOnStore(request.getProducts(), request.getStoreId());

        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.save(ReviewPostEntity.of(
                userEntity,
                request.getTitle(),
                request.getContent(),
                request.getRating()
        ));

        if(productEntities.size() > 0){
            reviewPostEntity.addProducts(productEntities);
        }

        addImages(request.getImages(), reviewPostEntity);
    }

    @Transactional
    public ReviewPost modify(ReviewPostModifyRequest request, Long postId, String username) {
        ReviewPostEntity reviewPostEntity = getReviewPostEntityOrException(postId);
        isPostMine(reviewPostEntity.getUser().getUsername(), username, postId);

        List<ProductEntity> productEntities = storeService.getProductListOnStore(request.getProducts(), request.getStoreId());

        reviewPostEntity.updateReviewPost(request.getTitle(), request.getContent(), request.getRating(), productEntities);

        addImages(request.getImages(), reviewPostEntity);

        return ReviewPost.fromEntity(reviewPostEntity);
    }

    @Transactional
    public void delete(Long postId, String username) {
        ReviewPostEntity reviewPostEntity = getReviewPostEntityOrException(postId);
        isPostMine(reviewPostEntity.getUser().getUsername(), username, postId);

        reviewPostEntity.clear();
        reviewPostEntityRepository.delete(reviewPostEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> list(Pageable pageable) {
        return reviewPostEntityRepository.findAll(pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> myList(String username, Pageable pageable) {
        return reviewPostEntityRepository.findAllByUser_Username(username, pageable).map(ReviewPost::fromEntity);
    }

    @Transactional
    public ReviewPostWithComment getPostAndComments(Long postId, String username) {
        userEntityRepositoryService.loadUserByUsername(username);
        ReviewPostEntity reviewPostEntity = getReviewPostEntityOrException(postId);

        if(viewsCacheRepository.getView(postId, username, false)) {
            viewsCacheRepository.setView(postId, username, false);
            reviewPostEntity.addViewer();
        }

        return ReviewPostWithComment.fromEntity(reviewPostEntity);
    }

    public ReviewPostEntity getReviewPostEntityOrException(Long postId) {
        return reviewPostEntityRepository.findById(postId).orElseThrow(
                () -> {throw new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId));}
        );
    }

    private void isPostMine(String writer, String username, Long postId){
        if(!Objects.equals(writer, username)){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }
    }

    public ReviewPost likeReviewPost(Long postId, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        ReviewPostEntity postEntity = getReviewPostEntityOrException(postId);

        ReviewPostLikeEntity reviewPostLikeEntity = reviewPostLikeEntityRepository.save(ReviewPostLikeEntity.of(postEntity, userEntity));
        postEntity.addLikes(reviewPostLikeEntity);

        return ReviewPost.fromEntity(postEntity);
    }

    private void addImages(List<Long> images, ReviewPostEntity reviewPostEntity) {
        int size = images.size();

        if (size > 0 && size < IMAGE_UPLOAD_MAX_COUNT) {
            reviewPostEntity.addImages(imageService.searchImages(images, ImageType.REVIEW).stream().map(ReviewImageEntity::fromImageEntity).collect(Collectors.toList()));
        } else if (size > IMAGE_UPLOAD_MAX_COUNT) {
            throw new GreenSumerBackApplicationException(ErrorCode.OVER_IMAGE_COUNT, String.format("Max Image count is 5, but requesting size is %s", size));
        }
    }
}
