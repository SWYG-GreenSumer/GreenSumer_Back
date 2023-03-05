package org.swyg.greensumer.repository.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViewsCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    public final static Duration CACHE_TTL = Duration.ofDays(1);

    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    public void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void setView(Long postId, String username, boolean flag) {
        this.redisTemplate.setValueSerializer(new StringRedisSerializer());
        this.valueOperations = redisTemplate.opsForValue();

        if (Objects.isNull(username) || Objects.isNull(postId)) {
            log.error("Required Values must not be null");
            return;
        }

        try {
            String key = getKey(postId, username, flag);
            valueOperations.set(key, 1, CACHE_TTL);

            log.info("[ViewsCacheRepository save - success]  postId: {}", postId);
        } catch (Exception e) {
            log.error("[ViewsCacheRepository save - error] {}", e.getMessage());
        }
    }

    public boolean getView(Long postId, String username, boolean flag) {
        this.redisTemplate.setValueSerializer(new StringRedisSerializer());
        this.valueOperations = redisTemplate.opsForValue();

        String key = getKey(postId, username, flag);
        Object o = valueOperations.get(key);

        log.info("Get data from Redis {}, {}", key, postId);
        return Objects.isNull(o);
    }

    public String getKey(Long postId, String username, boolean flag) {
        if (!flag)
            return "REVIEW-" + postId + "-" + username;
        else
            return "EVENT-" + postId + "-" + username;
    }

}
