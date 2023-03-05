package org.swyg.greensumer.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;
import org.swyg.greensumer.dto.User;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    public final static Duration CACHE_TTL = Duration.ofDays(7);

    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    public void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    public void setUser(User user){
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
        this.valueOperations = redisTemplate.opsForValue();

        if(Objects.isNull(user) || Objects.isNull(user.getUsername())){
            log.error("Required Values must not be null");
            return;
        }

        try{
            String key = getKey(user.getUsername());
            valueOperations.set(key, user, CACHE_TTL);
            log.info("[UserCacheRepository save - success]  id: {}", user.getId());
        }catch (Exception e){
            log.error("[UserCacheRepository save - error] {}", e.getMessage());
        }
    }

    public Optional<User> getUser(String username){
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
        this.valueOperations = redisTemplate.opsForValue();

        String key = getKey(username);
        User user = (User) valueOperations.get(key);
        log.info("Get data from Redis {}, {}", key, user);
        return Optional.ofNullable(user);
    }

    public void setRefreshToken(String username, String refreshToken) {
        String key = getRefreshTokenKey(username);
        valueOperations.set(key, refreshToken, CACHE_TTL);
    }

    public String getRefreshToken(String username) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        this.valueOperations = redisTemplate.opsForValue();
        String key = getRefreshTokenKey(username);
        return valueOperations.get(key).toString();
    }

    public String getKey(String username){
        return "USER:"+username;
    }

    public String getRefreshTokenKey(String username) {
        return "RTK:"+username;
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(getKey(username));
        redisTemplate.delete(getRefreshTokenKey(username));
    }

}
