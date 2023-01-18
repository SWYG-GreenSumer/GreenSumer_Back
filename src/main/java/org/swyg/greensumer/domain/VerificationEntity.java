package org.swyg.greensumer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Getter
@ToString
@Table(name = "verification", indexes = {
        @Index(name = "subject_idx", columnList = "subject", unique = true)
})
@Entity
public class VerificationEntity {

    @Setter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String subject;

    @Setter @Column(nullable = false, length = 6)
    private String code;

    @Setter private boolean status;

    @Setter private Timestamp startedAt;
    @Setter private Timestamp expiredAt;

    @PrePersist
    void startedAt() {
        this.startedAt = Timestamp.from(Instant.now());
        this.expiredAt = new Timestamp(System.currentTimeMillis() + 180_000);   // 3m
    }

    protected VerificationEntity() {}

    private VerificationEntity(String subject, String code) {
        this.subject = subject;
        this.code = code;
        this.status = false;
    }

    public static VerificationEntity of(String subject, String code) {
        return new VerificationEntity(subject, code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerificationEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
