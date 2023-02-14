package org.swyg.greensumer.repository.interview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;
import org.swyg.greensumer.dto.Interview;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InterviewCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    public void init() {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Interview>(Interview.class));
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void setInterviews(List<Interview> interviews) {
        if (Objects.isNull(interviews)) {
            log.error("Required data must not be null");
            return;
        }

        try {
            hashOperations.delete(getKey() + "SELLER");
            hashOperations.delete(getKey() + "USER");

            for (Interview interview : interviews) {
                hashOperations.put(getKey() + interview.getTarget().toString(), interview.getInterviewId().toString(), interview);
            }

            log.info("[InterviewCacheRepository save - success]");
        } catch (Exception e) {
            log.error("[InterviewCacheRepository save - error] {}", e.getMessage());
        }
    }

    public List<Interview> getInterviews() {
        try {
            List<Interview> list = new ArrayList<>();
            for (Object value : hashOperations.entries(getKey() + "SELLER").values()) {
                list.add((Interview) value);
            }

            for (Object value : hashOperations.entries(getKey() + "USER").values()) {
                list.add((Interview) value);
            }

            log.info("[InterviewCacheRepository findAll - success]");
            return list;

        } catch (Exception e) {
            log.error("[InterviewCacheRepository findAll - error] {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String getKey() {
        return "INTERVIEW";
    }

    public void modifyInterview(Long interviewId, Interview interview) {
        if (Objects.isNull(interviewId)) {
            log.error("Required field must not be null");
            return;
        }

        hashOperations.put(getKey(), interviewId.toString(), interview);
    }
}
