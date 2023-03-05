package org.swyg.greensumer.repository.images;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.Image;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageCacheRepository {

    private static final String CACHE_KEY = "IMAGE";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void setImages(ImageType type, List<ImageEntity> images) {
        this.hashOperations = redisTemplate.opsForHash();

        if (Objects.isNull(images) || Objects.isNull(type)) {
            log.error("Required subKey or value must not be null");
            return;
        }

        try {
            for (ImageEntity image : images) {
                hashOperations.put(CACHE_KEY, subKey(type, image.getId()), serializeImage(Image.fromEntity(image)));
            }

            log.info("[ImageCacheRepository save - success]");
        } catch (Exception e) {
            log.error("[ImageCacheRepository save - error] {}", e.getMessage());
        }
    }

    public Image getImage(ImageType type, Long id) {
        try{

            hashOperations = redisTemplate.opsForHash();
            String value = hashOperations.get(CACHE_KEY, subKey(type, id));

            return deserializeImage(value);

        } catch (Exception e) {
            log.error("[ImageCacheRepository getImage - error] {}", e.getMessage());
            return null;
        }
    }

    public void deleteImage(ImageType imageType, Long imageId) {
        hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(CACHE_KEY, subKey(imageType, imageId));
    }

    private String subKey(ImageType type, Long id) {
        return type.toString() + "-" + id;
    }

    private String serializeImage(Image image) throws JsonProcessingException {
        return objectMapper.writeValueAsString(image);
    }

    private Image deserializeImage(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, Image.class);
    }

}
