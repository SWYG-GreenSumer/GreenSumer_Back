package org.swyg.greensumer.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@MappedSuperclass
public abstract class DateTimeEntity {

    @Column(name = "registered_at") public Timestamp registeredAt;
    @Column(name = "updated_at") public Timestamp updatedAt;
    @Column(name = "deleted_at") public Timestamp deletedAt;

    @PrePersist void registeredAt() { this.registeredAt = Timestamp.from(Instant.now()); }
    @PreUpdate  void updatedAt() { this.updatedAt = Timestamp.from(Instant.now());}
}
