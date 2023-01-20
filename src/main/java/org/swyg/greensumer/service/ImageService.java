package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.Image;
import org.swyg.greensumer.dto.request.ImageModifyRequest;
import org.swyg.greensumer.dto.request.ImagesCreateRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ImageEntityRepository;
import org.swyg.greensumer.utils.ImageUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageEntityRepository imageEntityRepository;
    private final UserService userService;

    @Transactional
    public Image saveImage(MultipartFile image, String type, String username) throws IOException {
        UserEntity userEntity = userService.findByUsernameOrException(username);
        String originFilename = image.getOriginalFilename();
        String savedFilename = getSavedFilename(originFilename);

        if (Objects.isNull(image)) {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_IS_NULL, String.format("Image is Null"));
        }

        ImageEntity imageEntity = imageEntityRepository.save(
                ImageEntity.of(ImageType.valueOf(type), userEntity, originFilename, savedFilename, ImageUtils.compressImage(image.getBytes()))
        );

        return Image.fromEntity(imageEntity);
    }

    public Image searchImage(Integer imageId, String username) {
        userService.loadUserByUsername(username);

        ImageEntity imageEntity = getImageEntityOrException(imageId);
        imageEntity.setImageData(ImageUtils.decompressImage(imageEntity.getImageData()));

        return Image.fromEntity(imageEntity);
    }

    @Transactional
    public List<Image> saveImages(ImagesCreateRequest request, String username) throws IOException {
        UserEntity userEntity = userService.findByUsernameOrException(username);

        List<ImageEntity> imageEntities = new LinkedList<>();
        ImageType type = ImageType.valueOf(request.getType());

        for (MultipartFile image : request.getImages()) {
            String savedFilename = getSavedFilename(image.getOriginalFilename());

            ImageEntity entity = ImageEntity.of(type, userEntity, image.getOriginalFilename(), savedFilename, ImageUtils.compressImage(image.getBytes()));
            entity.setProduct(null);
            entity.setStore(null);
            ImageEntity save = imageEntityRepository.save(entity);

            imageEntities.add(save);
        }

        return imageEntities.stream().map(Image::fromEntity).collect(Collectors.toList());
    }

    public Image modifyImage(Integer imageId, ImageModifyRequest request, String username) throws IOException {
        userService.loadUserByUsername(username);

        ImageEntity imageEntity = getImageEntityOrException(imageId);

        imageEntity.setImageData(ImageUtils.compressImage(request.getImage().getBytes()));
        imageEntity.setImageType(ImageType.valueOf(request.getType()));

        return Image.fromEntity(imageEntity);
    }

    public void removeImage(Integer imageId, String username) {
        userService.loadUserByUsername(username);

        imageEntityRepository.deleteById(imageId);
    }

    private static String getSavedFilename(String filename) {
        return UUID.randomUUID() + "_" + filename;
    }

    private ImageEntity getImageEntityOrException(Integer imageId) {
        return imageEntityRepository.findById(imageId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("%s not founded", imageId));
        });
    }

    public List<ImageEntity> findAllByIdIn(List<Integer> imageIds) {
        return imageEntityRepository.findAllByIdIn(imageIds);
    }

}
