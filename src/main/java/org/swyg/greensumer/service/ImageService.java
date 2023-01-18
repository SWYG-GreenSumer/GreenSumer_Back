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
import org.swyg.greensumer.repository.UserEntityRepository;
import org.swyg.greensumer.utils.ImageUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageEntityRepository imageEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public Image saveImage(MultipartFile image, String type, String username) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        String savedFilename = UUID.randomUUID() + "_" + image.getOriginalFilename();

        ImageEntity imageEntity = imageEntityRepository.save(
                ImageEntity.of(ImageType.valueOf(type), userEntity, image.getOriginalFilename(), savedFilename, ImageUtils.compressImage(image.getBytes()))
        );

        if(imageEntity == null){
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_SAVE_FAIL, String.format("%s cant save", image.getOriginalFilename()));
        }

        return Image.fromEntity(imageEntity);
    }

    public Image searchImage(Integer imageId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        ImageEntity imageEntity = imageEntityRepository.findById(imageId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("%s not founded", imageId));
        });

        imageEntity.setImageData(ImageUtils.decompressImage(imageEntity.getImageData()));

        return Image.fromEntity(imageEntity);
    }

    @Transactional
    public List<Image> saveImages(ImagesCreateRequest request, String username) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        List<ImageEntity> imageEntities = new LinkedList<>();
        ImageType type = ImageType.valueOf(request.getType());

        for(MultipartFile image : request.getImages()){

            String savedFilename = UUID.randomUUID() + "_" + image.getOriginalFilename();

            ImageEntity entity = ImageEntity.of(type, userEntity, image.getOriginalFilename(), savedFilename, ImageUtils.compressImage(image.getBytes()));
            entity.setProduct(null);
            entity.setStore(null);
            ImageEntity save = imageEntityRepository.save(entity);

            imageEntities.add(save);
        }

        return imageEntities.stream().map(Image::fromEntity).collect(Collectors.toList());
    }

    public Image modifyImage(Integer imageId, ImageModifyRequest request, String username) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        ImageEntity imageEntity = imageEntityRepository.findById(imageId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND, String.format("%s not founded", imageId));
        });

        imageEntity.setImageData(ImageUtils.compressImage(request.getImage().getBytes()));
        imageEntity.setImageType(ImageType.valueOf(request.getType()));

        return Image.fromEntity(imageEntity);
    }

    public void removeImage(Integer imageId, String username) {
        userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        imageEntityRepository.deleteById(imageId);
    }
}
