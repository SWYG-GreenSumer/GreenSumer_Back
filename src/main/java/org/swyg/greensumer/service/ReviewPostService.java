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
import org.swyg.greensumer.dto.ReviewPostWithComment;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.ReviewPostCreateRequest;
import org.swyg.greensumer.dto.request.ReviewPostModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ReviewPostEntityRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ReviewPostService {

    private final ReviewPostEntityRepository reviewPostEntityRepository;
    private final UserService userService;
    private final StoreService storeService;
    private final ImageService imageService;

    public void create(ReviewPostCreateRequest request, Integer productId, String username) {
        UserEntity userEntity = userService.findByUsernameOrException(username);

        ProductEntity productEntity = storeService.getProductEntityOrException(productId);

        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.save(ReviewPostEntity.of(
                productEntity,
                userEntity,
                request.getTitle(),
                request.getContent()
        ));

        if(request.getImages().size() > 0){
            reviewPostEntity.addImages(imageService.findAllByIdIn(request.getImages()));
        }
    }

    @Transactional
    public ReviewPost modify(ReviewPostModifyRequest request, Integer postId, Integer productId, String username) {
        ReviewPostEntity reviewPostEntity = getReviewPostEntityOrException(postId);

        isPostMine(reviewPostEntity.getUser().getUsername(), username, postId);

        userService.loadUserByUsername(username);

        ProductEntity productEntity = storeService.getProductEntityOrException(productId);

        reviewPostEntity.setProduct(productEntity);
        reviewPostEntity.setTitle(request.getTitle());
        reviewPostEntity.setContent(request.getContent());

        if(request.getImages().size() > 0){
            reviewPostEntity.addImages(imageService.findAllByIdIn(request.getImages()));
        }

        return ReviewPost.fromEntity(reviewPostEntity);
    }

    @Transactional
    public void delete(Integer postId, String username) {
        ReviewPostEntity reviewPostEntity = getReviewPostEntityOrException(postId);

        userService.loadUserByUsername(username);
        isPostMine(reviewPostEntity.getUser().getUsername(), username, postId);

        reviewPostEntityRepository.delete(reviewPostEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> list(Pageable pageable) {
        return reviewPostEntityRepository.findAll(pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewPost> myList(String username, Pageable pageable) {
        User user = userService.loadUserByUsername(username);

        return reviewPostEntityRepository.findAllByUser_Id(user.getId(), pageable).map(ReviewPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public ReviewPostWithComment getPostAndComments(Integer postId, String username) {
        userService.loadUserByUsername(username);

        return reviewPostEntityRepository.findById(postId)
                .map(ReviewPostWithComment::fromEntity)
                .orElseThrow(() -> { throw new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId));});
    }

    public ReviewPostEntity getReviewPostEntityOrException(Integer postId) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.getReferenceById(postId);

        if(Objects.isNull(reviewPostEntity)){
            throw new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId));
        }

        return reviewPostEntity;
    }

    private void isPostMine(String writer, String username, Integer postId){
        if(writer != username){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }
    }

}
