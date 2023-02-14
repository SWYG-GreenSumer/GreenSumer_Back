package org.swyg.greensumer.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@MappedSuperclass
public abstract class CommentEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    protected UserEntity user;

    @Column(name = "content", columnDefinition = "TEXT")
    protected String content;

    public void update(String content) {
        this.content = content;
    }
}
