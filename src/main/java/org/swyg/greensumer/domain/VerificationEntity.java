package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@ToString
@Table(name = "\"verification\"", indexes = {
        @Index(name = "subject_idx", columnList = "subject", unique = true)
})
@Entity
public class VerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String subject;

    @Setter
    @Column(nullable = false, length = 6)
    private String code;

    private Timestamp startedAt;
    private Timestamp expiredAt;

    @PrePersist
    void startedAt() {
        this.startedAt = Timestamp.from(Instant.now());
        this.expiredAt = new Timestamp(System.currentTimeMillis() + 180_000);   // 3m
    }

    protected VerificationEntity() {}

    private VerificationEntity(String subject, String code) {
        this.subject = subject;
        this.code = code;
    }

    public static VerificationEntity of(String subject, String code) {
        return new VerificationEntity(subject, code);
    }
}
