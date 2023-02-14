package org.swyg.greensumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.dto.ZeroWasteShop;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZeroWasteShopRedisTemplateService {

    private static final String CACHE_KEY = "SHOP";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;  // Key, 약국 DB PK, 약국 Dto 를 Json 형태로 저장

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(ZeroWasteShop zeroWasteShop) {
        if(Objects.isNull(zeroWasteShop) || Objects.isNull(zeroWasteShop.getId())) {
            log.error("Required Values must not be null");
            return;
        }

        try{
            hashOperations.put(CACHE_KEY, zeroWasteShop.getId().toString(), serializeShopDto(zeroWasteShop));
            log.info("[ShopRedisTemplateService save - success]  id: {}", zeroWasteShop.getId());
        } catch (Exception e) {
            log.error("[ShopRedisTemplateService save - error] {}", e.getMessage());
        }
    }

    public List<ZeroWasteShop> findAll() {
        try {
            List<ZeroWasteShop> list = new ArrayList<>();

            for(String value : hashOperations.entries(CACHE_KEY).values()) {
                ZeroWasteShop zeroWasteShop = deserializeShopDto(value);
                list.add(zeroWasteShop);
            }

            return list;

        } catch (Exception e){
            log.error("[ShopRedisTemplateService findAll - error] {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[ShopRedisTemplateService delete - success]  id: {}", id);
    }

    private String serializeShopDto(ZeroWasteShop zeroWasteShop) throws JsonProcessingException {
        return objectMapper.writeValueAsString(zeroWasteShop);
    }

    private ZeroWasteShop deserializeShopDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, ZeroWasteShop.class);
    }
}
