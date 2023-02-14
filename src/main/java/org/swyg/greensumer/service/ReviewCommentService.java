package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.ReviewCommentEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewComment;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.review.ReviewCommentRepository;

@RequiredArgsConstructor
@Service
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewPostService reviewPostService;
    private final UserEntityRepositoryService userEntityRepositoryService;

    @Transactional
    public void createComment(Long postId, String content, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostService.getReviewPostEntityOrException(postId);
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        reviewCommentRepository.save(ReviewCommentEntity.of(
                reviewPostEntity,
                userEntity,
                content
        ));
    }

    @Transactional
    public ReviewComment modifyComment(Long postId, Long commentId, String content, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostService.getReviewPostEntityOrException(postId);
        ReviewCommentEntity reviewCommentEntity = getReviewCommentEntityOrException(commentId);

        if (!reviewCommentEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        reviewCommentEntity.update(content);

        return ReviewComment.fromEntity(reviewCommentEntity);
    }

    public void deleteComment(Long postId, Long commentId, String username) {
        ReviewPostEntity reviewPost = reviewPostService.getReviewPostEntityOrException(postId);
        ReviewCommentEntity reviewCommentEntity = getReviewCommentEntityOrException(commentId);

        if (!reviewCommentEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        reviewCommentRepository.deleteById(commentId);
    }

    private ReviewCommentEntity getReviewCommentEntityOrException(Long commentId){
        return reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND, String.format("%s not founded", commentId)));
    }

}
