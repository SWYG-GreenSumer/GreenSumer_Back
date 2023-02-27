package org.swyg.greensumer.repository.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.dto.Store;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCacheRepository {

    private static final String CACHE_KEY = "STORE";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;  // Key, Store DB PK, Store Dto 를 Json 형태로 저장

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(Store store) {
        if(Objects.isNull(store) || Objects.isNull(store.getId())) {
            log.error("Required Values must not be null");
            return;
        }

        try{
            hashOperations.put(CACHE_KEY, store.getId().toString(), serializeStore(store));
            log.info("[StoreCacheRepository save - success]  id: {}", store.getId());
        } catch (Exception e) {
            log.error("[StoreCacheRepository save - error] {}", e.getMessage());
        }
    }

    public List<Store> findAll() {
        try {
            List<Store> list = new ArrayList<>();

            for(String value : hashOperations.entries(CACHE_KEY).values()) {
                Store zeroWasteShop = deserializeStore(value);
                list.add(zeroWasteShop);
            }

            return list;

        } catch (Exception e){
            log.error("[StoreCacheRepository findAll - error] {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[StoreCacheRepository delete - success]  id: {}", id);
    }

    private String serializeStore(Store store) throws JsonProcessingException {
        return objectMapper.writeValueAsString(store);
    }

    private Store deserializeStore(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, Store.class);
    }
}
