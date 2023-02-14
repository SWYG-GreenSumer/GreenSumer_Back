package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.EventCommentEntity;
import org.swyg.greensumer.domain.EventPostEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.EventComment;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.event.EventCommentEntityRepository;

@RequiredArgsConstructor
@Service
public class EventCommentService {

    private final EventPostService eventPostService;

    private final EventCommentEntityRepository eventCommentEntityRepository;
    private final UserEntityRepositoryService userEntityRepositoryService;

    @Transactional
    public void createComment(Long postId, String content, String username) {
        EventPostEntity eventPostEntity = eventPostService.getEventPostEntityOrException(postId);
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        eventCommentEntityRepository.save(EventCommentEntity.builder()
                .eventPost(eventPostEntity)
                .user(userEntity)
                .content(content)
                .build()
        );
    }

    public EventComment modifyComment(Long postId, Long commentId, String content, String username) {
        EventPostEntity eventPostEntity = eventPostService.getEventPostEntityOrException(postId);
        EventCommentEntity eventCommentEntity = getEventCommentEntityOrException(commentId);
        if(!eventCommentEntity.getUser().getUsername().equals(username)){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        eventCommentEntity.update(content);

        return EventComment.fromEntity(eventCommentEntity);
    }

    public void deleteComment(Long postId, Long commentId, String username) {
        EventPostEntity eventPostEntity = eventPostService.getEventPostEntityOrException(postId);
        EventCommentEntity eventCommentEntity = getEventCommentEntityOrException(commentId);

        if (!eventCommentEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission", username));
        }

        eventCommentEntityRepository.deleteById(commentId);
    }

    private EventCommentEntity getEventCommentEntityOrException(Long commentId) {
        return eventCommentEntityRepository.findById(commentId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.COMMENT_NOT_FOUND, String.format("Event Commend: %s not founded", commentId));
        });
    }

}
