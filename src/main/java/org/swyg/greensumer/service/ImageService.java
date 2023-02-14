package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.Image;
import org.swyg.greensumer.dto.request.ImageModifyRequest;
import org.swyg.greensumer.dto.request.ImagesCreateRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.images.*;
import org.swyg.greensumer.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageService {
    public final static int IMAGE_UPLOAD_MAX_COUNT = 5;
    private final static long IMAGE_UPLOAD_MAX_SIZE = 10_000_001L;
    private final ImageCacheRepository imageCacheRepository;
    private final UserEntityRepositoryService userEntityRepositoryService;
    private final EventImageEntityRepository eventImageEntityRepository;
    private final ReviewImageEntityRepository reviewImageEntityRepository;
    private final ProductImageEntityRepository productImageEntityRepository;
    private final StoreImageEntityRepository storeImageEntityRepository;

    @Transactional
    public Image saveImage(MultipartFile image, String type, String username) throws IOException {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        if (Objects.isNull(image)) {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_IS_NULL, "Image is Null");
        }

        String originFilename = image.getOriginalFilename();
        String savedFilename = getSavedFilename(originFilename);

        validateImageSize(image.getSize(), originFilename);

        ImageEntity imageEntity = null;

        switch (ImageType.valueOf(type)) {
            case REVIEW -> {
                imageEntity = reviewImageEntityRepository.save(ReviewImageEntity.builder().userEntity(userEntity).originFilename(originFilename).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
            }
            case EVENT -> {
                imageEntity = eventImageEntityRepository.save(EventImageEntity.builder().userEntity(userEntity).originFilename(originFilename).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
            }
            case STORE -> {
                imageEntity = storeImageEntityRepository.save(StoreImageEntity.builder().userEntity(userEntity).originFilename(originFilename).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
            }
            case PRODUCT -> {
                imageEntity = productImageEntityRepository.save(ProductImageEntity.builder().userEntity(userEntity).originFilename(originFilename).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
            }
        }

        imageCacheRepository.setImages(ImageType.valueOf(type), List.of(imageEntity));

        return Image.fromEntity(imageEntity);
    }

    @Transactional
    public List<Image> saveImages(ImagesCreateRequest request, String type, String username) throws IOException {
        if (request.getImages().size() > IMAGE_UPLOAD_MAX_COUNT) {
            throw new GreenSumerBackApplicationException(ErrorCode.OVER_IMAGE_COUNT, String.format("Max Image count is 5, but requesting size is %s", request.getImages().size()));
        }

        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        List<ImageEntity> imageEntities = new ArrayList<>();
        ImageType imageType = ImageType.valueOf(type);

        for (MultipartFile image : request.getImages()) {
            validateImageSize(image.getSize(), image.getOriginalFilename());

            String savedFilename = getSavedFilename(image.getOriginalFilename());
            switch (imageType) {
                case REVIEW -> imageEntities.add(ReviewImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                case EVENT -> imageEntities.add(EventImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                case STORE -> imageEntities.add(StoreImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                case PRODUCT -> imageEntities.add(ProductImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
            }
        }

        switch (imageType) {
            case REVIEW -> {
                List<ReviewImageEntity> reviewImageEntities = reviewImageEntityRepository.saveAll(imageEntities.stream().map(ReviewImageEntity::fromImageEntity).collect(Collectors.toList()));
                imageEntities = reviewImageEntities.stream().map(ReviewImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case EVENT -> {
                List<EventImageEntity> eventImageEntities = eventImageEntityRepository.saveAll(imageEntities.stream().map(EventImageEntity::fromImageEntity).collect(Collectors.toList()));
                imageEntities = eventImageEntities.stream().map(EventImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case STORE -> {
                List<StoreImageEntity> storeImageEntities = storeImageEntityRepository.saveAll(imageEntities.stream().map(StoreImageEntity::fromImageEntity).collect(Collectors.toList()));
                imageEntities = storeImageEntities.stream().map(StoreImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case PRODUCT -> {
                List<ProductImageEntity> productImageEntities = productImageEntityRepository.saveAll(imageEntities.stream().map(ProductImageEntity::fromImageEntity).collect(Collectors.toList()));
                imageEntities = productImageEntities.stream().map(ProductImageEntity::fromImageEntity).collect(Collectors.toList());
            }
        }

        imageCacheRepository.setImages(imageType, imageEntities);

        return imageEntities.stream().map(Image::fromEntity).collect(Collectors.toList());
    }

    public byte[] searchImage(Long imageId, String type) {
       byte[] image = imageCacheRepository.getImage(ImageType.valueOf(type), imageId);

       return ImageUtils.decompressImage(image);
    }

    public List<ImageEntity> searchImages(List<Long> imageIds, ImageType type) {
        List<ImageEntity> images = imageCacheRepository.getImages(type, imageIds);

        images.forEach(e -> ImageUtils.decompressImage(e.getImageData()));

        return images;
    }

    private void validateImageSize(long size, String originalFilename) {
        if (size >= IMAGE_UPLOAD_MAX_SIZE) {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_SIZE_OVER, String.format("%s Image size exceeds 10 MB. Check the Image Size", originalFilename));
        }
    }

    @Transactional
    public void modifyImage(Long imageId, ImageModifyRequest request, String username) throws IOException {
        userEntityRepositoryService.loadUserByUsername(username);
        validateImageSize(request.getImage().getSize(), request.getImage().getOriginalFilename());

        ImageType imageType = ImageType.valueOf(request.getType());

        switch (ImageType.valueOf(request.getType())) {
            case REVIEW -> {
                ReviewImageEntity entity = reviewImageEntityRepository.findById(imageId).orElseThrow(() -> {
                    throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("Review Image %s not founded", imageId));
                });

                entity.update(ImageUtils.compressImage(request.getImage().getBytes()));
                imageCacheRepository.setImages(imageType, List.of(entity));
            }
            case EVENT -> {
                EventImageEntity entity = eventImageEntityRepository.findById(imageId).orElseThrow(() -> {
                    throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("Event Image %s not founded", imageId));
                });

                entity.update(ImageUtils.compressImage(request.getImage().getBytes()));
                imageCacheRepository.setImages(imageType, List.of(entity));
            }
            case STORE -> {
                StoreImageEntity entity = storeImageEntityRepository.findById(imageId).orElseThrow(() -> {
                    throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("Store Image %s not founded", imageId));
                });

                entity.update(ImageUtils.compressImage(request.getImage().getBytes()));
                imageCacheRepository.setImages(imageType, List.of(entity));
            }
            case PRODUCT -> {
                ProductImageEntity entity = productImageEntityRepository.findById(imageId).orElseThrow(() -> {
                    throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("Product Image %s not founded", imageId));
                });

                entity.update(ImageUtils.compressImage(request.getImage().getBytes()));
                imageCacheRepository.setImages(imageType, List.of(entity));
            }
        }

    }

    public void removeImage(Long imageId, String type, String username) {
        ImageType imageType = ImageType.valueOf(type);

        userEntityRepositoryService.loadUserByUsername(username);

        switch (imageType) {
            case REVIEW -> reviewImageEntityRepository.deleteById(imageId);
            case EVENT -> eventImageEntityRepository.deleteById(imageId);
            case STORE -> storeImageEntityRepository.deleteById(imageId);
            case PRODUCT -> productImageEntityRepository.deleteById(imageId);
        }
    }

    private static String getSavedFilename(String filename) {
        return UUID.randomUUID() + "_" + filename;
    }

}
