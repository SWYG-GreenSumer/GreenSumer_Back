package org.swyg.greensumer.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;
import org.swyg.greensumer.dto.Verification;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VerificationCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static Duration VERFICATION_CODE_TTL = Duration.ofMinutes(5);
    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    public void init() {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Verification>(Verification.class));
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void setVerification(Verification verification){
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Verification>(Verification.class));
        this.valueOperations = redisTemplate.opsForValue();

        if(Objects.isNull(verification)){
            log.error("Required Values must not be null");
            return;
        }

        try{
            String key = getKey(verification.getSubject());
            valueOperations.set(key, verification, VERFICATION_CODE_TTL);
            log.info("[VerificationCacheRepository save - success]  email: {}, code: {}", verification.getSubject(), verification.getCode());
        }catch (Exception e){
            log.error("[VerificationCacheRepository save - error] {}", e.getMessage());
        }
    }

    public Optional<Verification> getVerification(String email){
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Verification>(Verification.class));
        this.valueOperations = redisTemplate.opsForValue();

        String key = getKey(email);
        Verification verification = (Verification) valueOperations.get(key);
        log.info("Get data from Redis {}, {}", key, verification);
        return Optional.ofNullable(verification);
    }

    public String getKey(String email){
        return "VERIFICATION:"+email;
    }

}
