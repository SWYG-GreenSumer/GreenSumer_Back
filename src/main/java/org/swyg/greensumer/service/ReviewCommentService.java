package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.ReviewCommentEntity;
import org.swyg.greensumer.domain.ReviewPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.ReviewComment;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ReviewCommentRepository;
import org.swyg.greensumer.repository.ReviewPostEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

@RequiredArgsConstructor
@Service
public class ReviewCommentService {

    private final ReviewPostEntityRepository reviewPostEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    public void createComment(Integer postId, String content, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        reviewCommentRepository.save(ReviewCommentEntity.of(
                reviewPostEntity,
                userEntity,
                content
        ));
    }


    public ReviewComment modifyComment(Integer postId, Integer commentId, String content, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        ReviewCommentEntity reviewCommentEntity = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND, String.format("%s not founded", commentId)));

        if (!reviewCommentEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        reviewCommentEntity.setContent(content);
        ReviewCommentEntity updatedComment = reviewCommentRepository.saveAndFlush(reviewCommentEntity);

        return ReviewComment.fromEntity(updatedComment);
    }

    public void deleteComment(Integer postId, Integer commentId, String username) {
        ReviewPostEntity reviewPostEntity = reviewPostEntityRepository.findById(postId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        ReviewCommentEntity reviewCommentEntity = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND, String.format("%s not founded", commentId)));

        if (!reviewCommentEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        reviewCommentRepository.deleteById(commentId);
    }

}
