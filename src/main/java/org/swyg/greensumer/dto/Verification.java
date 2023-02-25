package org.swyg.greensumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Verification {
    private String subject;
    private String code;
    private Timestamp startedAt;
    private Timestamp expiredAt;

    @Builder
    public Verification(String subject, String code) {
        this.subject = subject;
        this.code = code;
        this.startedAt = Timestamp.from(Instant.now());
        this.expiredAt = new Timestamp(System.currentTimeMillis() + 180_000);   // 3m
    }

    public static Verification of(String subject, String code) {
        return new Verification(subject, code);
    }
}
