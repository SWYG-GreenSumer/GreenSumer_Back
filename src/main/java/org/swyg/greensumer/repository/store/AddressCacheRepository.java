package org.swyg.greensumer.repository.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.dto.Address;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCacheRepository {

    private static final String CACHE_KEY = "ADDRESS";

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, Object> hashOperations;

    public void saveAddress(Address address) {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Address>(Address.class));
        this.hashOperations = redisTemplate.opsForHash();

        if(Objects.isNull(address) || Objects.isNull(address.getId())) {
            log.error("Required Values must not be null");
            return;
        }

        try {
            this.hashOperations.put(CACHE_KEY, address.getId().toString(), address);
            log.info("[AddressCacheRepository save - success]  id: {}", address.getId());
        }catch (Exception e){
            log.error("[AddressCacheRepository save - error] {}", e.getMessage());
        }
    }

    public Optional<Address> findById(Long id) {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Address>(Address.class));
        this.hashOperations = redisTemplate.opsForHash();

        Address address = (Address) this.hashOperations.get(CACHE_KEY, id);
        log.info("Get data from Redis {}, {}", CACHE_KEY, id);
        return Optional.ofNullable(address);
    }

    public List<Address> findAll() {
        try {
            List<Address> list = new ArrayList<>();

            for (Object obj : hashOperations.entries(CACHE_KEY).values()) {
                list.add((Address) obj);
            }

            return list;
        } catch (Exception e) {
            log.error("[AddressCacheRepository findAll - error] {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[AddressCacheRepository delete - success]  id: {}", id);
    }
}
