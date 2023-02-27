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
import java.util.*;
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
    public void saveImages(ImagesCreateRequest request, String type, String username) throws IOException {
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
                case REVIEW -> {
                    ReviewImageEntity reviewImageEntity = reviewImageEntityRepository.save(ReviewImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                    imageEntities.add(reviewImageEntity);
                }
                case EVENT -> {
                    EventImageEntity eventImageEntity = eventImageEntityRepository.save(EventImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                    imageEntities.add(eventImageEntity);
                }
                case STORE -> {
                    StoreImageEntity storeImageEntity = storeImageEntityRepository.save(StoreImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                    imageEntities.add(storeImageEntity);
                }
                case PRODUCT -> {
                    ProductImageEntity productImageEntity = productImageEntityRepository.save(ProductImageEntity.builder().userEntity(userEntity).originFilename(image.getOriginalFilename()).savedFilename(savedFilename).imageData(ImageUtils.compressImage(image.getBytes())).build());
                    imageEntities.add(productImageEntity);
                }
            }
        }

        imageCacheRepository.setImages(imageType, imageEntities);
    }

    public byte[] searchImage(Long imageId, String type) {
        ImageType imageType = ImageType.valueOf(type);

        Image image = imageCacheRepository.getImage(imageType, imageId);

        if(!Objects.isNull(image))
           return ImageUtils.decompressImage(image.getImageData());

       switch (imageType) {
           case REVIEW -> {
               ReviewImageEntity reviewImageEntity = reviewImageEntityRepository.findById(imageId).orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND));
               imageCacheRepository.setImages(imageType, List.of(reviewImageEntity));

               return ImageUtils.decompressImage(reviewImageEntity.getImageData());
           }
           case EVENT -> {
               EventImageEntity eventImageEntity = eventImageEntityRepository.findById(imageId).orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND));
               imageCacheRepository.setImages(imageType, List.of(eventImageEntity));

               return ImageUtils.decompressImage(eventImageEntity.getImageData());
           }
           case STORE -> {
               StoreImageEntity storeImageEntity = storeImageEntityRepository.findById(imageId).orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND));
               imageCacheRepository.setImages(imageType, List.of(storeImageEntity));

               return ImageUtils.decompressImage(storeImageEntity.getImageData());
           }
           case PRODUCT -> {
               ProductImageEntity productImageEntity = productImageEntityRepository.findById(imageId).orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND));
               imageCacheRepository.setImages(imageType, List.of(productImageEntity));

               return ImageUtils.decompressImage(productImageEntity.getImageData());
           }
           default -> {
               return null;
           }
       }
    }

    public List<ImageEntity> searchImages(List<Long> imageIds, ImageType type) {

        switch (type) {
            case REVIEW -> {
                return reviewImageEntityRepository.findAllByIdIn(imageIds).stream().map(ReviewImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case EVENT -> {
                return eventImageEntityRepository.findAllByIdIn(imageIds).stream().map(EventImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case STORE -> {
                return storeImageEntityRepository.findAllByIdIn(imageIds).stream().map(StoreImageEntity::fromImageEntity).collect(Collectors.toList());
            }
            case PRODUCT -> {
                return productImageEntityRepository.findAllByIdIn(imageIds).stream().map(ProductImageEntity::fromImageEntity).collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
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

        imageCacheRepository.deleteImage(imageType, imageId);
    }

    private static String getSavedFilename(String filename) {
        return UUID.randomUUID() + "_" + filename;
    }

}
