package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.EventPost;
import org.swyg.greensumer.dto.EventPostWithComment;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.EventPostCreateRequest;
import org.swyg.greensumer.dto.request.EventPostModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.event.EventPostEntityRepository;
import org.swyg.greensumer.repository.event.EventPostLikeRepository;
import org.swyg.greensumer.repository.event.EventPostViewerEntityRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventPostService {

    private final EventPostEntityRepository eventPostEntityRepository;
    private final EventPostViewerEntityRepository eventPostViewerEntityRepository;
    private final EventPostLikeRepository eventPostLikeRepository;

    private final UserEntityRepositoryService userEntityRepositoryService;
    private final StoreService storeService;
    private final ImageService imageService;

    @Transactional
    public void create(EventPostCreateRequest request, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        storeService.isStoreManager(userEntity.getId(), request.getStoreId());
        List<ProductEntity> productEntities = storeService.getProductListOnStore(request.getProducts(), request.getStoreId());

        LocalDateTime startedAt = toLocalDateTime(request.getStartedAt());
        LocalDateTime endedAt = toLocalDateTime(request.getStartedAt());
        EventStatus eventStatus = VerifyEventStatus(startedAt, endedAt);

        EventPostEntity eventPostEntity = eventPostEntityRepository.save(EventPostEntity.builder()
                .user(userEntity)
                .title(request.getTitle())
                .content(request.getContent())
                .started_at(startedAt)
                .ended_at(endedAt)
                .status(eventStatus)
                .build());

        if (productEntities.size() > 0) {
            eventPostEntity.addProducts(productEntities);
        }

        addImages(request.getImages(), eventPostEntity);
    }

    @Transactional
    public EventPost modify(EventPostModifyRequest request, Long postId, String username) {
        if (request.getImages().size() > 5) {
            throw new GreenSumerBackApplicationException(ErrorCode.OVER_IMAGE_COUNT, String.format("Max Image count is 5, but requesting size is %s", request.getImages().size()));
        }

        EventPostEntity eventPostEntity = getEventPostEntityOrException(postId);
        List<ProductEntity> productEntities = storeService.getProductListOnStore(request.getProducts(), request.getStoreId());

        isEventMine(eventPostEntity.getUser().getUsername(), username, postId);

        LocalDateTime startedAt = toLocalDateTime(request.getStartedAt());
        LocalDateTime endedAt = toLocalDateTime(request.getStartedAt());
        EventStatus eventStatus = VerifyEventStatus(startedAt, endedAt);

        eventPostEntity.updateEventPost(productEntities, request.getTitle(), request.getContent(), startedAt, endedAt, eventStatus);

        addImages(request.getImages(), eventPostEntity);

        return EventPost.fromEntity(eventPostEntity);
    }

    @Transactional
    public void delete(Long postId, String username) {
        EventPostEntity eventPostEntity = getEventPostEntityOrException(postId);
        isEventMine(eventPostEntity.getUser().getUsername(), username, postId);

        eventPostEntity.clear();
        eventPostEntityRepository.delete(eventPostEntity);
    }

    @Transactional(readOnly = true)
    public Page<EventPost> list(Pageable pageable) {
        return eventPostEntityRepository.findAll(pageable).map(EventPost::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<EventPost> myList(String username, Pageable pageable) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        return eventPostEntityRepository.findAllByUser_Id(user.getId(), pageable).map(EventPost::fromEntity);
    }

    @Transactional
    public EventPostWithComment getPostAndComments(Long postId, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        EventPostEntity eventPostEntity = getEventPostEntityOrException(postId);

        EventPostViewerEntity eventPostViewerEntity = eventPostViewerEntityRepository.findByEvent_IdAndUser_Id(postId, userEntity.getId())
                .orElseGet(() -> eventPostViewerEntityRepository.save(EventPostViewerEntity.of(eventPostEntity, userEntity))
                );

        eventPostEntity.addViewer(eventPostViewerEntity);

        return EventPostWithComment.fromEntity(eventPostEntity);
    }

    public EventPostEntity getEventPostEntityOrException(Long postId) {
        return eventPostEntityRepository.findById(postId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.EVENT_NOT_FOUND, String.format("Event %s not founded", postId));
        });
    }

    private void isEventMine(String writer, String username, Long postId) {
        if (!Objects.equals(writer, username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }
    }

    public EventPost likeEventPost(Long postId, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        EventPostEntity eventPostEntity = getEventPostEntityOrException(postId);

        EventPostLikeEntity eventPostLikeEntity = eventPostLikeRepository.save(EventPostLikeEntity.of(eventPostEntity, userEntity));
        eventPostEntity.addLikes(eventPostLikeEntity);

        return EventPost.fromEntity(eventPostEntity);
    }

    private static EventStatus VerifyEventStatus(LocalDateTime startedAt, LocalDateTime endedAt) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startedAt)) {
            return EventStatus.READY;
        } else if (now.isAfter(startedAt) && now.isBefore(endedAt)) {
            return EventStatus.PROGRESSED;
        } else {
            return EventStatus.FINISHED;
        }
    }

    private LocalDateTime toLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }

    private void addImages(List<Long> images, EventPostEntity eventPostEntity) {
        int size = images.size();

        if (size > 0 && size < 5) {
            eventPostEntity.addImages(imageService.searchImages(images, ImageType.EVENT).stream().map(EventImageEntity::fromImageEntity).collect(Collectors.toList()));
        } else if (size > 5) {
            throw new GreenSumerBackApplicationException(ErrorCode.OVER_IMAGE_COUNT, String.format("Max Image count is 5, but requesting size is %s", size));
        }
    }
}
