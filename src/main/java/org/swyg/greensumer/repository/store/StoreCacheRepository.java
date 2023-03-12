package org.swyg.greensumer.repository.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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
    private HashOperations<String, String, Object> hashOperations;  // Key, Store DB PK, Store Dto 를 Json 형태로 저장

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(Store store) {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Store>(Store.class));

        if(Objects.isNull(store) || Objects.isNull(store.getId())) {
            log.error("Required Values must not be null");
            return;
        }

        try{
            hashOperations.put(CACHE_KEY, store.getId().toString(), store);
            log.info("[StoreCacheRepository save - success]  id: {}", store.getId());
        } catch (Exception e) {
            log.error("[StoreCacheRepository save - error] {}", e.getMessage());
        }
    }

    public List<Store> findAll() {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Store>(Store.class));

        try {
            List<Store> list = new ArrayList<>();

            for(Object object : hashOperations.entries(CACHE_KEY).values()) {
                Store zeroWasteShop = (Store) object;
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
}
